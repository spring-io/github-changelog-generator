FROM ubuntu:bionic-20181018

ARG root=.
ARG jar=target/github-release-notes-generator.jar

COPY ${jar} /github-release-notes-generator.jar

RUN apt-get update
RUN apt-get install --no-install-recommends -y ca-certificates curl
RUN rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /opt/openjdk
ENV PATH $JAVA_HOME/bin:$PATH
RUN mkdir -p /opt/openjdk && \
    cd /opt/openjdk && \
    curl -L https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b04/OpenJDK8U-jdk_x64_linux_hotspot_8u212b04.tar.gz | tar xz
