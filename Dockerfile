FROM gradle:jdk8 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM java:latest
COPY --from=builder /home/gradle/src/build/libs/doko-website-0.2.0.jar /code/doko.jar
COPY cert.p12 /code/cert.p12
WORKDIR /code
ENTRYPOINT ["java", "-jar", "doko.jar"]
