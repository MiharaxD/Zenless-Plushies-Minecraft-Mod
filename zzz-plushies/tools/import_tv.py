from pathlib import Path
import copy,json,subprocess,shutil
import imageio_ffmpeg
from PIL import Image

ROOT=Path(__file__).resolve().parents[1]
ASSET=ROOT.parent/'assets/retro-tv'
RES=ROOT/'src/main/resources'
def write(p,obj):
 p.parent.mkdir(parents=True,exist_ok=True)
 p.write_text(json.dumps(obj,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
m=json.loads((ASSET/'tv_retro_zzz_mod_export.json').read_text())
m['textures']={'0':'zzzplushies:block/tv_retro_zzz','screen':'zzzplushies:block/bangboo_screen','particle':'zzzplushies:block/tv_retro_zzz'}
screen=next(e for e in m['elements'] if e.get('name')=='tela')
# Fill the inner bezel, keeping the user's source model untouched.
screen['from']=[1,13,1]
screen['to']=[22,27,3]
screen['faces']['north']={'uv':[0,0,16,16],'texture':'#screen'}
tex=RES/'assets/zzzplushies/textures/block';tex.mkdir(parents=True,exist_ok=True)
shutil.copy2(ASSET/'tv_retro_zzz.png',tex/'tv_retro_zzz.png')
# Fill the 3:2 CRT canvas without added side pillars.
raw=subprocess.run([imageio_ffmpeg.get_ffmpeg_exe(),'-v','error','-i',str(ROOT.parent/'references/zenless-zone-zero-bangboo.mp4'),'-vf','fps=20,scale=96:64:flags=neighbor','-f','rawvideo','-pix_fmt','rgba','-'],capture_output=True,check=True).stdout
frame_bytes=96*64*4
assert len(raw)%frame_bytes==0
n=len(raw)//frame_bytes
atlas=Image.new('RGBA',(96,64*n))
for i in range(n): atlas.paste(Image.frombytes('RGBA',(96,64),raw[i*frame_bytes:(i+1)*frame_bytes]),(0,i*64))
atlas.save(tex/'bangboo_screen.png')
write(tex/'bangboo_screen.png.mcmeta',{'animation':{'width':96,'height':64,'frametime':1,'interpolate':False}})
write(RES/'assets/zzzplushies/models/block/gacha_machine.json',m)
write(RES/'assets/zzzplushies/models/block/gacha_machine_upper.json',{'parent':'minecraft:block/block','textures':{'particle':m['textures']['particle']},'elements':[]})
variants={}
for direction,rotation in [('north',0),('east',90),('south',180),('west',270)]:
 for half in ['lower','upper']:
  variants[f'facing={direction},half={half}']={'model':'zzzplushies:block/gacha_machine'+('_upper' if half=='upper' else ''),'y':rotation}
write(RES/'assets/zzzplushies/blockstates/gacha_machine.json',{'variants':variants})
write(RES/'assets/zzzplushies/models/item/gacha_machine.json',{'parent':'zzzplushies:block/gacha_machine'})
loot=RES/'data/zzzplushies/loot_tables/blocks/gacha_machine.json'
l=json.loads(loot.read_text());l['pools'][0]['conditions']=[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:block_state_property','block':'zzzplushies:gacha_machine','properties':{'half':'lower'}}];write(loot,l)
adds={'en_us':{'button.zzzplushies.spin_short':'Single Pull','button.zzzplushies.spin_ten_short':'10 Pulls','gui.zzzplushies.banner_limited':'Rate-Up Banner','gui.zzzplushies.banner_desc':'Boosted odds for your selected plush! Use the arrows to pick your S-rank target.'},'pt_br':{'button.zzzplushies.spin_short':'1 Tiro','button.zzzplushies.spin_ten_short':'10 Tiros','gui.zzzplushies.banner_limited':'Banner de Chance Aumentada','gui.zzzplushies.banner_desc':'Chance aumentada pro plush selecionado! Use as setas pra escolher seu alvo S.'}}
for lang,entries in adds.items():
 p=RES/f'assets/zzzplushies/lang/{lang}.json';obj=json.loads(p.read_text(encoding='utf-8'));obj.update(entries);write(p,obj)
print(f'Imported user model: {len(m["elements"])} cubes; {n} frames at 20 fps ({n/20}s).')
