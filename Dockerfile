FROM maven:3.9.0-eclipse-temurin-11
RUN mkdir /photon
WORKDIR /photon
COPY pom.xml /photon
COPY src /photon/src/
COPY .sauce /photon/.sauce/
CMD mvn clean install

#COPY . /photon/.
# COPY pom.xml /photon
# COPY src /photon/src/
# ENV SAUCE_USERNAME=eyalyovel
# ENV SAUCE_ACCESS_KEY =
# CMD ls


