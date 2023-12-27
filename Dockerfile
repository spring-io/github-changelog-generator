FROM ubuntu:focal-20231211

ARG root=.
ARG jar=target/github-changelog-generator.jar

COPY ${jar} /github-changelog-generator.jar

RUN export DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install --no-install-recommends -y tzdata ca-certificates curl jq
RUN ln -fs /usr/share/zoneinfo/UTC /etc/localtime
RUN dpkg-reconfigure --frontend noninteractive tzdata
RUN rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /opt/openjdk
ENV PATH $JAVA_HOME/bin:$PATH
RUN mkdir -p /opt/openjdk && \
    cd /opt/openjdk && \
    curl -L "https://github.com/bell-sw/Liberica/releases/download/8u392+9/bellsoft-jdk8u392+9-linux-amd64.tar.gz" | tar xz --strip-components=1
