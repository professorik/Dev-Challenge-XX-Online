FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=false

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
