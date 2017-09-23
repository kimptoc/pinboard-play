FROM openjdk:8

ENV PROJECT_HOME /appl/pinboard-play

RUN mkdir -p $PROJECT_HOME $PROJECT_HOME/app

WORKDIR $PROJECT_HOME
RUN wget https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.tgz && \
    tar xvf sbt-1.0.2.tgz && ls -l

RUN ls -l $PROJECT_HOME/sbt

ENV PATH $PROJECT_HOME/sbt/bin:$PATH

RUN sbt about

COPY . $PROJECT_HOME/app

WORKDIR $PROJECT_HOME/app

EXPOSE 9000

# based on https://semaphoreci.com/community/tutorials/dockerizing-a-java-play-application