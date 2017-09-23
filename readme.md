A very thing wrapper for http://pinboard.in

Gives a lightweight view of your bookmarks.

Read only!

To run this via Docker:

First build it.
$ docker build -t pinboard-play .

Then run it.
$ docker run -i -p 9000:9000 pinboard-play sbt run

The view is pretty ugly.
Logout does not really work yet.