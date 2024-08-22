FROM amazoncorretto:21-alpine

WORKDIR /app

RUN mkdir logs

ADD ./build/libs/park-api.jar /app/park-api.jar

VOLUME /app/logs
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar -Dfile.encoding=UTF-8 -Dspring.profiles.active=${PARK_PROFILE} -Djasypt.encryptor.password=${PARK_PASSWORD} park-api.jar"]