FROM adoptopenjdk:11-jre-hotspot
RUN addgroup --system spring && adduser --system spring && usermod -aG spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
CMD ["java", "-jar", "/app.jar"]
