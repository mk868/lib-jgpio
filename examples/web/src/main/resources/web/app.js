const chipsElement = document.getElementById('chips');
const chipTemplate = document.getElementById('chip-template');
const lineTemplate = document.getElementById('line-template');
const connection = document.getElementById('connection');
const filter = document.getElementById('filter');
const dialog = document.getElementById('line-dialog');
const error = document.getElementById('error');

const MAX_ROWS = 18;

const rows = new Map();
const lines = new Map();
let selected = null;

const key = (chip, offset) => `${chip}/${offset}`;
const clone = (template) => template.content.firstElementChild.cloneNode(true);

function stateOf(line) {
  if (line.owned) {
    return line.direction.toLowerCase();
  }
  return line.used ? 'used' : 'free';
}

const events = new EventSource('api/events');
events.addEventListener('open', () => setConnection('live', 'Live'));
events.addEventListener('error', () => setConnection('lost', 'Reconnecting'));
events.addEventListener('snapshot', (event) => renderSnapshot(JSON.parse(event.data)));
events.addEventListener('line', (event) => {
  const line = JSON.parse(event.data);
  renderLine(line);
  flash(rows.get(key(line.chip, line.offset)));
});

function setConnection(state, text) {
  connection.dataset.state = state;
  connection.textContent = text;
  chipsElement.classList.toggle('stale', state !== 'live');
}

function renderSnapshot(snapshot) {
  rows.clear();
  lines.clear();
  chipsElement.replaceChildren(...snapshot.chips.map(renderChip));
  snapshot.lines.forEach(renderLine);
  layoutChips();
  if (selected && !lines.has(selected)) {
    dialog.close();
  }
}

function renderChip(chip) {
  const section = clone(chipTemplate);
  section.querySelector('.chip-name').textContent = chip.name;
  section.querySelector('.chip-label').textContent = chip.label;
  const list = section.querySelector('.lines');
  for (let offset = 0; offset < chip.lines; offset++) {
    const row = clone(lineTemplate);
    row.dataset.key = key(chip.name, offset);
    rows.set(row.dataset.key, row);
    list.append(row);
  }
  return section;
}

function renderLine(line) {
  const lineKey = key(line.chip, line.offset);
  const row = rows.get(lineKey);
  if (!row) {
    return;
  }
  lines.set(lineKey, line);
  row.dataset.state = stateOf(line);
  row.dataset.direction = line.direction === 'OUTPUT' ? 'out' : 'in';
  row.dataset.value = line.value;
  row.querySelector('.offset').textContent = line.offset;
  row.querySelector('.name').textContent = line.name ?? '';
  row.querySelector('.direction').textContent = row.dataset.direction;
  row.querySelector('.consumer').textContent = line.used ? line.consumer ?? '?' : '';
  row.querySelector('.value-text').textContent =
    line.value === null ? '' : line.value ? 'high' : 'low';
  row.querySelector('.row').title = `${line.name ?? 'unnamed'}, offset ${line.offset}`
    + (line.used ? `, used by ${line.consumer ?? '?'}` : '');
  row.querySelector('.switch').checked = line.value === true;
  if (filterRow(row)) {
    layoutChips();
  }
  renderSummary(row.closest('.chip'));
  if (lineKey === selected) {
    renderDialog(line);
  }
}

function renderSummary(section) {
  const count = (selector) => section.querySelectorAll(selector).length;
  const used = count('.line[data-state=used]');
  const owned = count('.line[data-state=input], .line[data-state=output]');
  section.querySelector('.chip-summary').textContent =
    `${count('.line')} lines · ${used} used · ${owned} requested here`;
}

function flash(row) {
  const button = row.querySelector('.row');
  const color = getComputedStyle(button).getPropertyValue('--flash');
  button.animate([{ offset: 0, backgroundColor: color }], { duration: 800, easing: 'ease-out' });
}

function layoutChips() {
  const sections = [...chipsElement.children];
  // without the columns added for boxes wider than the grid
  sections.forEach((section) => section.style.setProperty('--span', 1));
  const gridColumns = getComputedStyle(chipsElement).gridTemplateColumns.split(' ').length;
  for (const section of sections) {
    const visibleLines = section.querySelectorAll('.line:not([hidden])').length;
    const span = Math.max(1, Math.min(Math.ceil(visibleLines / MAX_ROWS), gridColumns));
    section.style.setProperty('--span', span);
    const names = section.querySelector('.column-names');
    while (names.children.length < span) {
      names.append(names.firstElementChild.cloneNode(true));
    }
    while (names.children.length > span) {
      names.lastElementChild.remove();
    }
  }
}

new ResizeObserver(layoutChips).observe(chipsElement);

function renderDialog(line) {
  dialog.dataset.state = stateOf(line);
  dialog.querySelector('#dialog-title').textContent = line.name ?? `Line ${line.offset}`;
  const field = (name) => dialog.querySelector(`[data-field=${name}]`);
  field('line').textContent = `${line.chip}, offset ${line.offset}`;
  field('direction').textContent = line.direction.toLowerCase();
  field('consumer').textContent = line.used ? line.consumer ?? 'unknown' : '–';
  if (line.value !== null) {
    field('value').textContent = line.value ? 'high' : 'low';
  } else {
    field('value').textContent = line.used ? 'unknown' : 'request the line as input to read it';
  }
  dialog.querySelector('[name=bias]').value = line.bias ?? '';
  dialog.querySelector('[name=drive]').value = line.drive ?? '';
  dialog.querySelector('[name=value]').checked = line.value === true;
}

chipsElement.addEventListener('click', (event) => {
  const button = event.target.closest('.line > .row');
  if (button) {
    selected = button.parentElement.dataset.key;
    renderDialog(lines.get(selected));
    dialog.showModal();
  }
});

dialog.addEventListener('close', () => selected = null);

dialog.addEventListener('click', (event) => {
  if (event.target === dialog) {
    dialog.close();
    return;
  }
  const button = event.target.closest('button[data-action]');
  if (button) {
    send(selected, button.dataset.action);
  }
});

document.addEventListener('change', (event) => {
  const control = event.target;
  if (control.matches('.line .switch')) {
    send(control.closest('.line').dataset.key, 'write', { value: control.checked });
  } else if (control.matches('dialog [data-action]')) {
    const value = control.type === 'checkbox' ? control.checked : control.value;
    send(selected, control.dataset.action, { [control.name]: value });
  }
});

async function send(lineKey, action, params = {}) {
  const { chip, offset } = lines.get(lineKey);
  const query = new URLSearchParams(params);
  try {
    const response = await fetch(
      `api/lines/${encodeURIComponent(chip)}/${offset}/${action}?${query}`,
      { method: 'POST' },
    );
    if (!response.ok) {
      throw new Error(await response.text() || response.statusText);
    }
  } catch (e) {
    showError(e.message);
    renderLine(lines.get(lineKey));
  }
}

let errorTimeout;

function showError(message) {
  error.textContent = message;
  if (!error.matches(':popover-open')) {
    error.showPopover();
  }
  clearTimeout(errorTimeout);
  errorTimeout = setTimeout(() => {
    if (error.matches(':popover-open')) {
      error.hidePopover();
    }
  }, 5000);
}

filter.addEventListener('input', () => {
  rows.forEach(filterRow);
  layoutChips();
});

function filterRow(row) {
  const line = lines.get(row.dataset.key);
  const text = filter.value.trim().toLowerCase();
  const searched = line ? `${line.offset} ${line.name ?? ''} ${line.consumer ?? ''}` : '';
  const hidden = !searched.toLowerCase().includes(text);
  const changed = row.hidden !== hidden;
  row.hidden = hidden;
  const section = row.closest('.chip');
  section.hidden = !section.querySelector('.line:not([hidden])');
  return changed;
}
