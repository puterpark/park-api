FROM amazoncorretto:21-alpine

ADD ./build/libs/park-api.jar /home/user/park-api.jar

ENTRYPOINT ["java", "-jar", "-Dfile.encoding=UTF-8", "-Dspring.profiles.active=${PARK_PROFILE}", "-Djasypt.encryptor.password=${PARK_PASSWORD}", "/home/user/park-api.jar"]