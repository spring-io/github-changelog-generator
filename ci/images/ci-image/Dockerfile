FROM ubuntu:focal-20220404

ADD setup.sh /setup.sh
RUN ./setup.sh

ENV JAVA_HOME /opt/openjdk
ENV PATH $JAVA_HOME/bin:$PATH
