/*
 * Copyright 2024-2026 SOFT-POL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.softpol.lib.jgpio.mock;

public class MockComplexTest {

  public void test() {
    var jgpio = JgpioMock.builder()
        .chip("gpiochip0", c -> c
                .line(0, l -> l.externallyRequested(MockLineRequest.asInput("kernel")))
                .line(1, l -> l.externallyRequested(MockLineRequest.asInput("kernel")))
                .line(2, l -> l.externallyRequested(MockLineRequest.asInput("kernel")))
                .line(3, l -> l.externallyRequested(MockLineRequest.asInput("kernel")))
//    line   0:      unnamed       kernel   input  active-high [used]
//    line   1:      unnamed       kernel   input  active-high [used]
//    line   2:      unnamed       unused   input  active-high
//    line   3:      unnamed       unused   input  active-high
//    line   4:      unnamed "orangepi:red:power" output active-high [used]
//    line   5:      unnamed       unused   input  active-high
//    line   6:      unnamed       unused   input  active-high
//    line   7:      unnamed "orangepi:green:status" output active-high [used]
//    line   8:      unnamed       unused   input  active-high
//    line   9:      unnamed       kernel   input  active-high [used]
//    line  10:      unnamed       unused   input  active-high
//    line  11:      unnamed       unused   input  active-high
//    line  12:      unnamed       unused   input  active-high
//    line  13:      unnamed       unused   input  active-high
//    line  14:      unnamed       unused   input  active-high
//    line  15:      unnamed       unused   input  active-high
//    line  16:      unnamed       unused   input  active-high
//    line  17:      unnamed       unused   input  active-high
//    line  18:      unnamed       unused   input  active-high
//    line  19:      unnamed       unused   input  active-high
//    line  20:      unnamed       unused   input  active-high
//    line  21:      unnamed       unused   input  active-high
//    line  22:      unnamed       unused   input  active-high
//    line  23:      unnamed       unused   input  active-high
//    line  24:      unnamed       unused   input  active-high
//    line  25:      unnamed       unused   input  active-high
//    line  26:      unnamed       unused   input  active-high
//    line  27:      unnamed       unused   input  active-high
//    line  28:      unnamed       unused   input  active-high
//    line  29:      unnamed       unused   input  active-high
//    line  30:      unnamed       unused   input  active-high
//    line  31:      unnamed       unused   input  active-high
//    line  32:      unnamed       unused   input  active-high
//    line  33:      unnamed       unused   input  active-high
//    line  34:      unnamed       unused   input  active-high
//    line  35:      unnamed       unused   input  active-high
//    line  36:      unnamed       unused   input  active-high
//    line  37:      unnamed       unused   input  active-high
//    line  38:      unnamed       unused   input  active-high
//    line  39:      unnamed       unused   input  active-high
//    line  40:      unnamed       unused   input  active-high
//    line  41:      unnamed       unused   input  active-high
//    line  42:      unnamed       unused   input  active-high
//    line  43:      unnamed       unused   input  active-high
//    line  44:      unnamed       unused   input  active-high
//    line  45:      unnamed       unused   input  active-high
//    line  46:      unnamed       unused   input  active-high
//    line  47:      unnamed       unused   input  active-high
//    line  48:      unnamed       unused   input  active-high
//    line  49:      unnamed       unused   input  active-high
//    line  50:      unnamed       unused   input  active-high
//    line  51:      unnamed       unused   input  active-high
//    line  52:      unnamed       unused   input  active-high
//    line  53:      unnamed       unused   input  active-high
//    line  54:      unnamed       unused   input  active-high
//    line  55:      unnamed       unused   input  active-high
//    line  56:      unnamed       unused   input  active-high
//    line  57:      unnamed       unused   input  active-high
//    line  58:      unnamed       unused   input  active-high
//    line  59:      unnamed       unused   input  active-high
//    line  60:      unnamed       unused   input  active-high
//    line  61:      unnamed       unused   input  active-high
//    line  62:      unnamed       unused   input  active-high
//    line  63:      unnamed       unused   input  active-high
        )
        .chip(1, c -> c
                .label("")
//    line   0:      unnamed       unused   input  active-high
//    line   1:      unnamed       unused   input  active-high
//    line   2:      unnamed       unused   input  active-high
//    line   3:      unnamed       unused   input  active-high
//    line   4:      unnamed       unused   input  active-high
//    line   5:      unnamed       unused   input  active-high
//    line   6:      unnamed       unused   input  active-high
//    line   7:      unnamed       unused   input  active-high
//    line   8:      unnamed       unused   input  active-high
//    line   9:      unnamed       unused   input  active-high
//    line  10:      unnamed       unused   input  active-high
//    line  11:      unnamed       unused   input  active-high
//    line  12:      unnamed       unused   input  active-high
//    line  13:      unnamed       unused   input  active-high
//    line  14:      unnamed       unused   input  active-high
//    line  15:      unnamed       unused   input  active-high
//    line  16:      unnamed       unused   input  active-high
//    line  17:      unnamed       unused   input  active-high
//    line  18:      unnamed       unused   input  active-high
//    line  19:      unnamed       unused   input  active-high
//    line  20:      unnamed       unused   input  active-high
//    line  21:      unnamed       unused   input  active-high
//    line  22:      unnamed       unused   input  active-high
//    line  23:      unnamed       unused   input  active-high
//    line  24:      unnamed       unused   input  active-high
//    line  25:      unnamed       unused   input  active-high
//    line  26:      unnamed       unused   input  active-high
//    line  27:      unnamed       unused   input  active-high
//    line  28:      unnamed       unused   input  active-high
//    line  29:      unnamed       unused   input  active-high
//    line  30:      unnamed       unused   input  active-high
//    line  31:      unnamed       unused   input  active-high
//    line  32:      unnamed       unused   input  active-high
//    line  33:      unnamed       unused   input  active-high
//    line  34:      unnamed       unused   input  active-high
//    line  35:      unnamed       unused   input  active-high
//    line  36:      unnamed       unused   input  active-high
//    line  37:      unnamed       unused   input  active-high
//    line  38:      unnamed       unused   input  active-high
//    line  39:      unnamed       unused   input  active-high
//    line  40:      unnamed       unused   input  active-high
//    line  41:      unnamed       unused   input  active-high
//    line  42:      unnamed       unused   input  active-high
//    line  43:      unnamed       unused   input  active-high
//    line  44:      unnamed       unused   input  active-high
//    line  45:      unnamed       unused   input  active-high
//    line  46:      unnamed       unused   input  active-high
//    line  47:      unnamed       unused   input  active-high
//    line  48:      unnamed       unused   input  active-high
//    line  49:      unnamed       unused   input  active-high
//    line  50:      unnamed       unused   input  active-high
//    line  51:      unnamed       unused   input  active-high
//    line  52:      unnamed       unused   input  active-high
//    line  53:      unnamed       unused   input  active-high
//    line  54:      unnamed       unused   input  active-high
//    line  55:      unnamed       unused   input  active-high
//    line  56:      unnamed       unused   input  active-high
//    line  57:      unnamed       unused   input  active-high
//    line  58:      unnamed       unused   input  active-high
//    line  59:      unnamed       unused   input  active-high
//    line  60:      unnamed       unused   input  active-high
//    line  61:      unnamed       unused   input  active-high
//    line  62:      unnamed       unused   input  active-high
//    line  63:      unnamed       unused   input  active-high
//    line  64:       "PC00"       unused   input  active-high
//    line  65:       "PC01"       unused   input  active-high
//    line  66:       "PC02"       unused   input  active-high
//    line  67:       "PC03"       unused   input  active-high
//    line  68:       "PC04"       unused   input  active-high
//    line  69:       "PC05"       unused   input  active-high
//    line  70:       "PC06" "usb0_id_det" input active-high [used]
//    line  71:       "PC07"       unused   input  active-high
//    line  72:       "PC08"       unused   input  active-high
//    line  73:       "PC09"       unused   input  active-high
//    line  74:       "PC10"       unused   input  active-high
//    line  75:       "PC11"       unused   input  active-high
//    line  76:       "PC12"       unused   input  active-high
//    line  77:       "PC13"       unused   input  active-high
//    line  78:       "PC14"       unused   input  active-high
//    line  79:       "PC15"       unused   input  active-high
//    line  80:       "PC16"       unused   input  active-high
//    line  81:      unnamed       unused   input  active-high
//    line  82:      unnamed       unused   input  active-high
//    line  83:      unnamed       unused   input  active-high
//    line  84:      unnamed       unused   input  active-high
//    line  85:      unnamed       unused   input  active-high
//    line  86:      unnamed       unused   input  active-high
//    line  87:      unnamed       unused   input  active-high
//    line  88:      unnamed       unused   input  active-high
//    line  89:      unnamed       unused   input  active-high
//    line  90:      unnamed       unused   input  active-high
//    line  91:      unnamed       unused   input  active-high
//    line  92:      unnamed       unused   input  active-high
//    line  93:      unnamed       unused   input  active-high
//    line  94:      unnamed       unused   input  active-high
//    line  95:      unnamed       unused   input  active-high
//    line  96:       "PD00"       unused   input  active-high
//    line  97:       "PD01"       unused   input  active-high
//    line  98:       "PD02"       unused   input  active-high
//    line  99:       "PD03"       unused   input  active-high
//    line 100:       "PD04"       unused   input  active-high
//    line 101:       "PD05"       unused   input  active-high
//    line 102:       "PD06"   "gmac-3v3"  output  active-high [used]
//    line 103:       "PD07"       unused   input  active-high
//    line 104:       "PD08"       unused   input  active-high
//    line 105:       "PD09"       unused   input  active-high
//    line 106:       "PD10"       unused   input  active-high
//    line 107:       "PD11"       unused   input  active-high
//    line 108:       "PD12"       unused   input  active-high
//    line 109:       "PD13"       unused   input  active-high
//    line 110:       "PD14"       unused   input  active-high
//    line 111:       "PD15"       unused   input  active-high
//    line 112:       "PD16"       unused   input  active-high
//    line 113:       "PD17"       unused   input  active-high
//    line 114:       "PD18"       unused   input  active-high
//    line 115:       "PD19"       unused   input  active-high
//    line 116:       "PD20"       unused   input  active-high
//    line 117:       "PD21"       unused   input  active-high
//    line 118:       "PD22"       unused  output  active-high
//    line 119:       "PD23"       unused   input  active-high
//    line 120:       "PD24"       unused   input  active-high
//    line 121:       "PD25"       unused   input  active-high
//    line 122:       "PD26"       unused   input  active-high
//    line 123:      unnamed       unused   input  active-high
//    line 124:      unnamed       unused   input  active-high
//    line 125:      unnamed       unused   input  active-high
//    line 126:      unnamed       unused   input  active-high
//    line 127:      unnamed       unused   input  active-high
//    line 128:      unnamed       unused   input  active-high
//    line 129:      unnamed       unused   input  active-high
//    line 130:      unnamed       unused   input  active-high
//    line 131:      unnamed       unused   input  active-high
//    line 132:      unnamed       unused   input  active-high
//    line 133:      unnamed       unused   input  active-high
//    line 134:      unnamed       unused   input  active-high
//    line 135:      unnamed       unused   input  active-high
//    line 136:      unnamed       unused   input  active-high
//    line 137:      unnamed       unused   input  active-high
//    line 138:      unnamed       unused   input  active-high
//    line 139:      unnamed       unused   input  active-high
//    line 140:      unnamed       unused   input  active-high
//    line 141:      unnamed       unused   input  active-high
//    line 142:      unnamed       unused   input  active-high
//    line 143:      unnamed       unused   input  active-high
//    line 144:      unnamed       unused   input  active-high
//    line 145:      unnamed       unused   input  active-high
//    line 146:      unnamed       unused   input  active-high
//    line 147:      unnamed       unused   input  active-high
//    line 148:      unnamed       unused   input  active-high
//    line 149:      unnamed       unused   input  active-high
//    line 150:      unnamed       unused   input  active-high
//    line 151:      unnamed       unused   input  active-high
//    line 152:      unnamed       unused   input  active-high
//    line 153:      unnamed       unused   input  active-high
//    line 154:      unnamed       unused   input  active-high
//    line 155:      unnamed       unused   input  active-high
//    line 156:      unnamed       unused   input  active-high
//    line 157:      unnamed       unused   input  active-high
//    line 158:      unnamed       unused   input  active-high
//    line 159:      unnamed       unused   input  active-high
//    line 160:       "PF00"       unused   input  active-high
//    line 161:       "PF01"       unused   input  active-high
//    line 162:       "PF02"       unused   input  active-high
//    line 163:       "PF03"       unused   input  active-high
//    line 164:       "PF04"       unused   input  active-high
//    line 165:       "PF05"       unused   input  active-high
//    line 166:       "PF06"         "cd"   input   active-low [used]
//    line 167:      unnamed       unused   input  active-high
//    line 168:      unnamed       unused   input  active-high
//    line 169:      unnamed       unused   input  active-high
//    line 170:      unnamed       unused   input  active-high
//    line 171:      unnamed       unused   input  active-high
//    line 172:      unnamed       unused   input  active-high
//    line 173:      unnamed       unused   input  active-high
//    line 174:      unnamed       unused   input  active-high
//    line 175:      unnamed       unused   input  active-high
//    line 176:      unnamed       unused   input  active-high
//    line 177:      unnamed       unused   input  active-high
//    line 178:      unnamed       unused   input  active-high
//    line 179:      unnamed       unused   input  active-high
//    line 180:      unnamed       unused   input  active-high
//    line 181:      unnamed       unused   input  active-high
//    line 182:      unnamed       unused   input  active-high
//    line 183:      unnamed       unused   input  active-high
//    line 184:      unnamed       unused   input  active-high
//    line 185:      unnamed       unused   input  active-high
//    line 186:      unnamed       unused   input  active-high
//    line 187:      unnamed       unused   input  active-high
//    line 188:      unnamed       unused   input  active-high
//    line 189:      unnamed       unused   input  active-high
//    line 190:      unnamed       unused   input  active-high
//    line 191:      unnamed       unused   input  active-high
//    line 192:      unnamed       unused   input  active-high
//    line 193:      unnamed       unused   input  active-high
//    line 194:      unnamed       unused   input  active-high
//    line 195:      unnamed       unused   input  active-high
//    line 196:      unnamed       unused   input  active-high
//    line 197:      unnamed       unused   input  active-high
//    line 198:      unnamed       unused   input  active-high
//    line 199:      unnamed       unused   input  active-high
//    line 200:      unnamed       unused   input  active-high
//    line 201:      unnamed       unused   input  active-high
//    line 202:      unnamed       unused   input  active-high
//    line 203:      unnamed       unused   input  active-high
//    line 204:      unnamed       unused   input  active-high
//    line 205:      unnamed       unused   input  active-high
//    line 206:      unnamed       unused   input  active-high
//    line 207:      unnamed       unused   input  active-high
//    line 208:      unnamed       unused   input  active-high
//    line 209:      unnamed       unused   input  active-high
//    line 210:      unnamed       unused   input  active-high
//    line 211:      unnamed       unused   input  active-high
//    line 212:      unnamed       unused   input  active-high
//    line 213:      unnamed       unused   input  active-high
//    line 214:      unnamed       unused   input  active-high
//    line 215:      unnamed       unused   input  active-high
//    line 216:      unnamed       unused   input  active-high
//    line 217:      unnamed       unused   input  active-high
//    line 218:      unnamed       unused   input  active-high
//    line 219:      unnamed       unused   input  active-high
//    line 220:      unnamed       unused   input  active-high
//    line 221:      unnamed       unused   input  active-high
//    line 222:      unnamed       unused   input  active-high
//    line 223:      unnamed       unused   input  active-high
//    line 224:       "PH00"       unused   input  active-high
//    line 225:       "PH01"       unused   input  active-high
//    line 226:       "PH02"     "ddc-en"  output  active-high [used]
//    line 227:       "PH03"       unused   input  active-high
//    line 228:       "PH04"       unused   input  active-high
//    line 229:       "PH05"       unused   input  active-high
//    line 230:       "PH06"       unused   input  active-high
//    line 231:       "PH07"       unused   input  active-high
//    line 232:       "PH08"       unused   input  active-high
//    line 233:       "PH09"       unused   input  active-high
//    line 234:       "PH10"       unused   input  active-high
//    line 235:      unnamed       unused   input  active-high
//    line 236:      unnamed       unused   input  active-high
//    line 237:      unnamed       unused   input  active-high
//    line 238:      unnamed       unused   input  active-high
//    line 239:      unnamed       unused   input  active-high
//    line 240:      unnamed       unused   input  active-high
//    line 241:      unnamed       unused   input  active-high
//    line 242:      unnamed       unused   input  active-high
//    line 243:      unnamed       unused   input  active-high
//    line 244:      unnamed       unused   input  active-high
//    line 245:      unnamed       unused   input  active-high
//    line 246:      unnamed       unused   input  active-high
//    line 247:      unnamed       unused   input  active-high
//    line 248:      unnamed       unused   input  active-high
//    line 249:      unnamed       unused   input  active-high
//    line 250:      unnamed       unused   input  active-high
//    line 251:      unnamed       unused   input  active-high
//    line 252:      unnamed       unused   input  active-high
//    line 253:      unnamed       unused   input  active-high
//    line 254:      unnamed       unused   input  active-high
//    line 255:      unnamed       unused   input  active-high
        )
        .build();
  }

}
