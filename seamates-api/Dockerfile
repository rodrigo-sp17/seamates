#############################################
#   Build Stage
#############################################
FROM gradle:7.4-jdk11-alpine as build

RUN mkdir -p /app
WORKDIR /app

COPY --chown=gradle:gradle . ./
RUN gradle assemble


#############################################
#   Run Stage
#############################################
FROM openjdk:11-jre as run

RUN mkdir /app

COPY --from=build /app/build/libs/*.jar /app/seamates-api.jar

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "/app/seamates-api.jar"]

