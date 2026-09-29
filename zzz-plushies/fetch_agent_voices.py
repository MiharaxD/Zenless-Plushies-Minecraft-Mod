import json, re, urllib.parse, urllib.request, time
from pathlib import Path
lang=json.loads(Path('src/main/resources/assets/zzzplushies/lang/en_us.json').read_text(encoding='utf-8'))
ids=re.findall(r'"([a-z0-9_]+)"',Path('src/main/java/dev/yuri/zzzplushies/PlushCatalog.java').read_text())
names={id:lang['block.zzzplushies.'+id].removesuffix(' Plush') for id in ids}
names['starlight_billy']='Billy Kid'
names['rina']='Alexandrina Sebastiane'
base='https://zenless-zone-zero.fandom.com/api.php?'
def api(params):
 u=base+urllib.parse.urlencode(dict(params,format='json'))
 req=urllib.request.Request(u,headers={'User-Agent':'Mozilla/5.0 (personal fan mod source verification)'})
 for attempt in range(4):
  try:
   with urllib.request.urlopen(req,timeout=30) as r:return json.load(r)
  except Exception as e:
   if attempt==3:raise
   time.sleep(2*(attempt+1))
results={}
for i in range(0,len(ids),10):
 part=ids[i:i+10]
 titles=[names[id]+'/Voice-Overs' for id in part]
 q=api({'action':'query','prop':'revisions','rvprop':'content','rvslots':'main','titles':'|'.join(titles),'redirects':1})['query']
 pages={p['title'].lower():p for p in q['pages'].values()}
 redirects={r['from'].lower():r['to'].lower() for r in q.get('redirects',[])}
 normalized={r['from'].lower():r['to'].lower() for r in q.get('normalized',[])}
 for id,title in zip(part,titles):
  key=title.lower();key=normalized.get(key,key);key=redirects.get(key,key)
  p=pages.get(key,{})
  text=p.get('revisions',[{}])[0].get('slots',{}).get('main',{}).get('*','')
  agent=re.search(r'\|agent\s*=\s*([^\r\n|]+)',text)
  tx=re.search(r'\|gacha-getchar_1_tx\s*=\s*([^\r\n|]+)',text)
  file=re.search(r'\|gacha-getchar_1_file\s*=\s*([^\r\n|]+)',text)
  results[id]={'name':names[id],'page':p.get('title',title),'found':bool(text),'agent':agent.group(1).strip() if agent else None,'text':tx.group(1).strip() if tx else None,'file_override':file.group(1).strip() if file else None}
 print(i+len(part),'/',len(ids),flush=True)
Path('voice_page_audit.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
for id,r in results.items():print(id, r)
