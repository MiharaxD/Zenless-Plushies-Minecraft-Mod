import json,urllib.parse,urllib.request,time
from pathlib import Path
pages=json.loads(Path('voice_page_audit.json').read_text(encoding='utf-8'))
base='https://zenless-zone-zero.fandom.com/api.php?'
def api(q):
 u=base+urllib.parse.urlencode(dict(q,format='json'))
 req=urllib.request.Request(u,headers={'User-Agent':'Mozilla/5.0 (personal fan mod source verification)'})
 for attempt in range(4):
  try:
   with urllib.request.urlopen(req,timeout=30) as r:return json.load(r)
  except Exception:
   if attempt==3:raise
   time.sleep(2*(attempt+1))
ids=[id for id,p in pages.items() if p['found'] and p['agent']]
results={}
for i in range(0,len(ids),12):
 part=ids[i:i+12]
 titles={id:'File:VO_'+pages[id]['agent'].replace(' ','_')+'_Obtain_Agent_01.ogg' for id in part}
 q=api({'action':'query','prop':'imageinfo','iiprop':'url|size','titles':'|'.join(titles.values())})['query']
 by_title={p['title'].casefold():p for p in q['pages'].values()}
 normalized={x['from'].casefold():x['to'].casefold() for x in q.get('normalized',[])}
 for id,title in titles.items():
  p=by_title.get(normalized.get(title.casefold(),title.casefold()),{})
  info=p.get('imageinfo',[{}])[0]
  results[id]={'file':p.get('title',title),'url':info.get('url'),'size':info.get('size'),'page':pages[id]['page'],'text':pages[id]['text']}
 print(i+len(part),'/',len(ids),flush=True)
Path('voice_file_audit.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
print('with_url',sum(bool(r['url']) for r in results.values()),'missing',[(id,r['file']) for id,r in results.items() if not r['url']])
