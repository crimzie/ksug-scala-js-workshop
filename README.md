An example project showing off autowire + scala.js + http4s.

### compiling client code
To compile client code in `root/js/` subproject to JavaScript run `sbt fastOptJS`. 
This will emit a `client-fastopt.js` script in target folder. This project uses 
a shortcut to allow the backend to serve emitted script immediately from target 
folder: in `root/jvm/src/main/scala/example/Server.scala` on line 55 there is an
absolute path to scala.js compile output hardcoded into an endpoint. Change this 
to whatever path `sbt fastOptJS` prints or replace this with proper deployment 
for a real project.

#running in dev
`sbt rottJVM/run` will start the server and bind http to port 8080. go to 
`localhost:8080` to try it out.
