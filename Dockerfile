FROM java:latest
VOLUME /tmp
ADD . /code
WORKDIR /code
ENTRYPOINT ["java", "-jar", "build/libs/doko-website-0.2.0.jar"]
