import subprocess,os,time,json,threading,re,sys,email
from pathlib import Path
from http.server import ThreadingHTTPServer,SimpleHTTPRequestHandler
from aiosmtpd.controller import Controller
import requests
R=Path(__file__).resolve().parents[1];os.chdir(R);sessions=[];logs=[];checks=[]
s=requests.Session();s.trust_env=False
class Mail:
 token=''
 async def handle_DATA(self,server,session,envelope):
  msg=email.message_from_bytes(envelope.content);body=msg.get_payload(decode=True).decode('utf-8');self.token=re.search(r'\n([A-Za-z0-9_-]{43})\r?\n',body).group(1);return '250 OK'
mail=Mail();smtp=Controller(mail,hostname='127.0.0.1',port=1025)
class Server(SimpleHTTPRequestHandler):
 def __init__(self,*a,**k):super().__init__(*a,directory=str(R/'frontend/dist/foro/browser'),**k)
 def log_message(self,*a):pass
 def route(self):
  if self.path.startswith('/__qa/mail-token'):
   b=json.dumps({'token':mail.token}).encode();self.send_response(200);self.send_header('Content-Type','application/json');self.end_headers();self.wfile.write(b);return
  if self.path.startswith('/api/'):
   port=8081 if re.match(r'/api/(auth|usuarios|roles)(/|\?|$)',self.path) else 8083 if self.path.startswith('/api/moderaciones') else 8082
   body=self.rfile.read(int(self.headers.get('Content-Length',0)));headers={k:v for k,v in self.headers.items() if k.lower() not in ['host','connection','content-length','accept-encoding']}
   r=s.request(self.command,f'http://127.0.0.1:{port}'+self.path,data=body,headers=headers,timeout=20)
   self.send_response(r.status_code);self.send_header('Content-Type',r.headers.get('Content-Type','application/json'));self.end_headers();self.wfile.write(r.content);return
  if not (R/'frontend/dist/foro/browser'/self.path.lstrip('/').split('?')[0]).is_file():self.path='/index.html'
  super().do_GET()
 do_GET=do_POST=do_PUT=do_DELETE=route

def req(port,method,path,data=None,token=None,status=200):
 r=s.request(method,f'http://127.0.0.1:{port}'+path,json=data,headers={'Authorization':'Bearer '+token} if token else {},timeout=15)
 assert r.status_code==status,(method,path,r.status_code,r.text[:500]);return r.json() if r.content else None
def check(msg):checks.append(msg);print('OK',msg,flush=True)
try:
 smtp.start()
 for name,port in [('usuarios',8081),('foro',8082),('moderacion',8083)]:
  log=open(R/f'evidencias/runtime_{name}.log','w');logs.append(log)
  sessions.append(subprocess.Popen(['java','-Xmx256m','-jar',str(R/f'microservicios/{name}-service/target/{name}-service-1.0.0.jar'),'--spring.profiles.active=demo'],stdout=log,stderr=subprocess.STDOUT))
 for port in [8081,8082,8083]:
  for i in range(120):
   try:
    if s.get(f'http://127.0.0.1:{port}/actuator/health',timeout=1).status_code==200:break
   except requests.RequestException:pass
   time.sleep(1)
  else:raise RuntimeError('No inició puerto '+str(port))
 check('Tres aplicaciones Spring Boot iniciadas con perfil demo H2')
 # Original collection validates preserved S2 contract.
 with open(R/'evidencias/09_postman_newman.log','w') as log:
  p=subprocess.run(['node',str(R/'qa/node_modules/newman/bin/newman.js'),'run',str(R/'postman/Foro_DSY2205.postman_collection.json'),'-e',str(R/'postman/Local.postman_environment.json'),'--reporters','cli','--timeout-request','15000'],stdout=log,stderr=subprocess.STDOUT,env={**os.environ,'HTTP_PROXY':'','HTTPS_PROXY':'','ALL_PROXY':'','NO_PROXY':'localhost,127.0.0.1'})
  assert p.returncode==0,'Newman original falló'
 check('Colección Postman original completa')
 with open(R/'evidencias/09_postman_s5.log','w') as log:
  p=subprocess.run(['node',str(R/'qa/node_modules/newman/bin/newman.js'),'run',str(R/'postman/S5_Moderacion.postman_collection.json'),'-e',str(R/'postman/Local.postman_environment.json'),'--reporters','cli','--timeout-request','15000'],stdout=log,stderr=subprocess.STDOUT,env={**os.environ,'HTTP_PROXY':'','HTTPS_PROXY':'','ALL_PROXY':'','NO_PROXY':'localhost,127.0.0.1'})
  assert p.returncode==0,'Newman S5 falló'
 check('Colección Postman S5 completa')
 admin=req(8081,'POST','/api/auth/login',{'email':'admin@foro.local','password':'ForoDemo2026!'})['accessToken'];member=req(8081,'POST','/api/auth/login',{'email':'miembro@foro.local','password':'ForoDemo2026!'})['accessToken']
 req(8083,'GET','/api/moderaciones',token=member,status=403);req(8083,'GET','/api/moderaciones',status=401);check('Moderación: autenticación obligatoria y rol ADMIN')
 t=req(8082,'POST','/api/temas',{'titulo':'Integración moderación','contenido':'Contenido para comprobar bloqueo','categoriaId':1},member,201)['id']
 c=req(8082,'POST',f'/api/temas/{t}/comentarios',{'contenido':'Comentario a moderar'},member,201)['id']
 d=req(8083,'POST','/api/moderaciones',{'tipo':'COMENTARIO','recursoId':c,'motivo':'Prueba'},admin,201)['id'];req(8082,'GET',f'/api/comentarios/{c}',token=member,status=404)
 assert req(8082,'GET',f'/api/temas/{t}/comentarios',token=member)['totalElements']==0
 req(8083,'DELETE',f'/api/moderaciones/{d}',token=admin,status=204);req(8082,'GET',f'/api/comentarios/{c}',token=member);check('Comentario bloqueado oculto en GET individual y listado; DELETE restaura')
 d=req(8083,'POST','/api/moderaciones',{'tipo':'TEMA','recursoId':t,'motivo':'Prueba'},admin,201)['id'];req(8082,'GET',f'/api/temas/{t}',token=member,status=404);req(8082,'POST',f'/api/temas/{t}/comentarios',{'contenido':'No debe permitir'},member,404)
 assert req(8082,'GET','/api/temas?q=Integraci%C3%B3n%20moderaci%C3%B3n',token=member)['totalElements']==0
 assert req(8083,'GET',f'/api/moderaciones/{d}',token=admin)['bloqueado']
 req(8083,'PUT',f'/api/moderaciones/{d}',{'motivo':'Restaurar','bloqueado':False},admin);req(8082,'GET',f'/api/temas/{t}',token=member);req(8083,'DELETE',f'/api/moderaciones/{d}',token=admin,status=204);req(8082,'DELETE',f'/api/temas/{t}',token=member,status=204);check('CRUD moderación y propagación real al foro: tema oculto, respuestas rechazadas y restauración')
 req(8081,'POST','/api/auth/registro',{'nombre':'Recuperacion','email':'recovery@foro.local','password':'ResetAnterior2026!'},status=201)
 a=req(8081,'POST','/api/auth/recuperar',{'email':'recovery@foro.local'});b=req(8081,'POST','/api/auth/recuperar',{'email':'ausente@foro.local'});assert a==b
 token=mail.token;assert len(token)==43;req(8081,'POST','/api/auth/restablecer',{'token':token,'password':'ResetNueva2026!'})
 req(8081,'POST','/api/auth/restablecer',{'token':token,'password':'ResetNueva2026!'},status=400);req(8081,'POST','/api/auth/login',{'email':'recovery@foro.local','password':'ResetAnterior2026!'},status=401);req(8081,'POST','/api/auth/login',{'email':'recovery@foro.local','password':'ResetNueva2026!'});check('SMTP real: respuesta genérica, código de un uso, contraseña antigua rechazada y nueva aceptada')
 (R/'evidencias/09_integracion_http.json').write_text(json.dumps({'entorno':'Tres procesos Java, H2, SMTP local; no Oracle ni Docker','checks':checks},ensure_ascii=False,indent=2))
 server=ThreadingHTTPServer(('127.0.0.1',4200),Server);threading.Thread(target=server.serve_forever,daemon=True).start()
 subprocess.run(['node',str(R/'qa/browser.cjs')],check=True)
finally:
 for p in sessions:p.terminate()
 for p in sessions:
  try:p.wait(timeout=15)
  except subprocess.TimeoutExpired:p.kill()
 for log in logs:log.close()
 smtp.stop()
