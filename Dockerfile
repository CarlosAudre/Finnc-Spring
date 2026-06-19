#Create a docker image with maven version and jdk version (jdk version must be the same as the project version)
FROM maven:3.9.9-eclipse-temurin-17 AS build
# Copy all files in src to /app/src (inside the container)
COPY src /app/src
#Copy pom.xml in /app (container)
COPY pom.xml /app
#Switch to /app directory ->
WORKDIR /app
#install java dependencies
RUN mvn clean package -DskipTests
#Copy JDK version into container
FROM eclipse-temurin:17-jdk-jammy
#Copy the installed java file into /app
COPY --from=build /app/target/*.jar /app/app.jar
#return to /app
WORKDIR /app
EXPOSE 8081
#Command that will run the application ("app.jar is the app name)
CMD ["java", "-jar", "app.jar"]


