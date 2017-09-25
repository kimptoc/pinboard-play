A very thing wrapper for http://pinboard.in

Gives a lightweight view of your bookmarks.

See it running here: https://pinboard.kimptoc.net - you need to login with your pinboard credentials!

Read only!

To run this via Docker:

First build it.
$ docker build -t pinboard-play .

Then run it.
$ docker run --rm --name pinboard-play -i -p 9000:9000 pinboard-play sbt run

setup prod image
$ sudo docker build -t pinboard-play-prod prod

run prod image
$ sudo docker run --rm --name pinboard-play -i -p 9000:9000 pinboard-play-prod target/universal/stage/bin/pinboard-play -Dplay.crypto.secret=0123456789

The view is pretty ugly.
Logout does not really work yet.
