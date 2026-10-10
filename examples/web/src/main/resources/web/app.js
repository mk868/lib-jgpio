// Renders the chips and lines sent by the server as Server-Sent Events and sends the line controls
// to the server API, see LineApi.java

const chipsElement = document.getElementById('chips');
const chipTemplate = document.getElementById('chip-template');
const lineTemplate = document.getElementById('line-template');
const connection = document.getElementById('connection');
const filter = document.getElementById('filter');
const dialog = document.getElementById('line-dialog');
const error = document.getElementById('error');

/** Line tiles by line key `chip/offset` */
const tiles = new Map();
/** Last line states received from the server by line key */
const lines = new Map();
/** Key of the line shown in the dialog */
let selected = null;

const key = (chip, offset) => `${chip}/${offset}`;
const clone = (template) => template.content.firstElementChild.cloneNode(true);

/** Which controls are shown, see style.css */
function stateOf(line) {
  if (line.owned) {
    return line.direction.toLowerCase();
  }
  return line.used ? 'used' : 'free';
}

// --- events from the server

const events = new EventSource('api/events');
events.addEventListener('open', () => setConnection('live', 'Live'));
events.addEventListener('error', () => setConnection('lost', 'Reconnecting'));
events.addEventListener('snapshot', (event) => renderSnapshot(JSON.parse(event.data)));
events.addEventListener('line', (event) => {
  const line = JSON.parse(event.data);
  renderLine(line);
  flash(tiles.get(key(line.chip, line.offset)));
});

function setConnection(state, text) {
  connection.dataset.state = state;
  connection.textContent = text;
  chipsElement.classList.toggle('stale', state !== 'live');
}

function renderSnapshot(snapshot) {
  tiles.clear();
  lines.clear();
  chipsElement.replaceChildren(...snapshot.chips.map(renderChip));
  snapshot.lines.forEach(renderLine);
  if (selected && !lines.has(selected)) {
    dialog.close();
  }
}

function renderChip(chip) {
  const section = clone(chipTemplate);
  // sizes the chip box, see style.css
  section.style.setProperty('--lines', chip.lines);
  section.querySelector('.chip-name').textContent = chip.name;
  section.querySelector('.chip-label').textContent = chip.label;
  const list = section.querySelector('.lines');
  for (let offset = 0; offset < chip.lines; offset++) {
    const tile = clone(lineTemplate);
    tile.dataset.key = key(chip.name, offset);
    tiles.set(tile.dataset.key, tile);
    list.append(tile);
  }
  return section;
}

function renderLine(line) {
  const lineKey = key(line.chip, line.offset);
  const tile = tiles.get(lineKey);
  if (!tile) {
    return;
  }
  lines.set(lineKey, line);
  tile.dataset.state = stateOf(line);
  tile.dataset.value = line.value;
  tile.querySelector('.offset').textContent = line.offset;
  tile.querySelector('.name').textContent = line.name ?? '';
  const direction = line.direction === 'OUTPUT' ? 'out' : 'in';
  tile.querySelector('.info').textContent = line.used
    ? `${direction} · ${line.consumer ?? '?'}`
    : direction;
  tile.querySelector('.tile').title = `${line.name ?? 'unnamed'}, offset ${line.offset}`
    + (line.used ? `, used by ${line.consumer ?? '?'}` : '');
  tile.querySelector('.switch').checked = line.value === true;
  filterTile(tile);
  if (lineKey === selected) {
    renderDialog(line);
  }
}

function flash(tile) {
  const button = tile.querySelector('.tile');
  const color = getComputedStyle(button).getPropertyValue('--flash');
  // fades from the color to the background set by style.css
  button.animate([{ offset: 0, backgroundColor: color }], { duration: 800, easing: 'ease-out' });
}

// --- line dialog

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
  const tile = event.target.closest('.tile');
  if (tile) {
    selected = tile.closest('.line').dataset.key;
    renderDialog(lines.get(selected));
    dialog.showModal();
  }
});

dialog.addEventListener('close', () => selected = null);

dialog.addEventListener('click', (event) => {
  // a click outside the dialog body hits the backdrop
  if (event.target === dialog) {
    dialog.close();
    return;
  }
  const button = event.target.closest('button[data-action]');
  if (button) {
    send(selected, button.dataset.action);
  }
});

// --- line controls, in the dialog and the switches of output tiles

document.addEventListener('change', (event) => {
  const control = event.target;
  if (control.matches('.line .switch')) {
    send(control.closest('.line').dataset.key, 'write', { value: control.checked });
  } else if (control.matches('dialog [data-action]')) {
    const value = control.type === 'checkbox' ? control.checked : control.value;
    send(selected, control.dataset.action, { [control.name]: value });
  }
});

/** Sends the action, the new line state comes back as an event */
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
    // undo the change of the switch or select
    renderLine(lines.get(lineKey));
  }
}

let errorTimeout;

function showError(message) {
  error.textContent = message;
  // a popover is shown above the dialog
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

// --- filter

filter.addEventListener('input', () => tiles.forEach(filterTile));

function filterTile(tile) {
  const line = lines.get(tile.dataset.key);
  const text = filter.value.trim().toLowerCase();
  const searched = line ? `${line.offset} ${line.name ?? ''} ${line.consumer ?? ''}` : '';
  tile.hidden = !searched.toLowerCase().includes(text);
  // hide chips without matching lines
  const section = tile.closest('.chip');
  section.hidden = !section.querySelector('.line:not([hidden])');
}
