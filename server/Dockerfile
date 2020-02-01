FROM openjdk:15-jdk-buster
COPY . /usr/server
WORKDIR /usr/server

ENV ANT_VERSION 1.10.6
RUN cd && \
    wget -q http://www.us.apache.org/dist//ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
    tar -xzf apache-ant-${ANT_VERSION}-bin.tar.gz && \
    mv apache-ant-${ANT_VERSION} /opt/ant && \
    rm apache-ant-${ANT_VERSION}-bin.tar.gz
ENV ANT_HOME /opt/ant
ENV PATH ${PATH}:/opt/ant/bin

EXPOSE 43594
EXPOSE 43595
EXPOSE 43596
EXPOSE 43597
EXPOSE 43598
EXPOSE 43599

CMD ant compile_core
CMD ant compile_plugins

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

CMD /wait

ENTRYPOINT ["ant","runserver"]