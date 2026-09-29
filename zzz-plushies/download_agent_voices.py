import json,urllib.request,time,hashlib
from pathlib import Path
j=json.loads(Path('voice_file_audit.json').read_text(encoding='utf-8'))
out=Path('src/main/resources/assets/zzzplushies/sounds/voices');out.mkdir(parents=True,exist_ok=True)
seen={}
for id,item in j.items():
 url=item['url']
 if not url:continue
 if url in seen:
  (out/(id+'.ogg')).write_bytes((out/(seen[url]+'.ogg')).read_bytes())
  continue
 for attempt in range(4):
  try:
   with urllib.request.urlopen(urllib.request.Request(url,headers={'User-Agent':'Mozilla/5.0','Referer':'https://zenless-zone-zero.fandom.com/'}),timeout=30) as r:data=r.read()
   if not data.startswith(b'OggS'):raise ValueError('not Ogg: '+id)
   (out/(id+'.ogg')).write_bytes(data);seen[url]=id
   print(id,len(data),flush=True)
   break
  except Exception as e:
   if attempt==3:print('FAILED',id,e,flush=True)
   else:time.sleep(2*(attempt+1))
print('downloaded',len(list(out.glob('*.ogg'))))
