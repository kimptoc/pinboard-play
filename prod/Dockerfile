FROM pinboard-play

RUN sbt clean stage

ENV PATH $PROJECT_HOME/target/universal/stage/bin:$PATH

ENTRYPOINT target/universal/stage/bin/pinboard-play -Dplay.crypto.secret=0123456789
