FROM amazoncorretto:21-alpine

WORKDIR /app

RUN mkdir logs

COPY ./build/libs/park-api.jar /app/park-api.jar

ENV TZ=Asia/Seoul
ENV PARK_PROFILE=""
ENV PARK_PASSWORD=""

VOLUME /app/logs
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar -Dfile.encoding=UTF-8 -Dspring.profiles.active=${PARK_PROFILE} -Djasypt.encryptor.password=${PARK_PASSWORD} park-api.jar"]
