# File: backend/Dockerfile
# This Dockerfile builds a Docker image for your Spring Boot backend application.

# 1. Use an official OpenJDK image as the base for the build stage.
#    We choose a slim version for a smaller image size.
FROM eclipse-temurin:17-jdk-jammy as build

# 2. Set the working directory inside the container for the build process.
WORKDIR /app

# 3. Install Maven.
#    apt-get update: Updates the package list.
#    apt-get install -y maven: Installs Maven. The -y flag answers yes to prompts.
RUN apt-get update && apt-get install -y maven

# 4. Copy the Maven build files (pom.xml) first.
#    This allows Docker to cache the dependencies layer.
COPY pom.xml .

# 5. Copy the rest of your project's source code.
#    The '.' means copy everything from the current host directory into the /app directory in the container.
COPY src ./src

# 6. Build the Spring Boot application using Maven.
#    -DskipTests: Skips running tests during the Docker build for speed.
#                  In a real CI/CD pipeline, tests would run in a separate stage.
RUN mvn clean install -DskipTests


# 7. 'package' stage: Create a smaller runtime image.
#    We use a JRE-only image, as the JDK is not needed at runtime.
FROM eclipse-temurin:17-jre-jammy

# 8. Set the working directory for the runtime application.
WORKDIR /app

# 9. Copy the built JAR file from the 'build' stage into the runtime image.
#    'chat-0.0.1-SNAPSHOT-0.0.1-SNAPSHOT.jar' is the actual generated file name.
COPY --from=build /app/target/chat-0.0.1-SNAPSHOT-0.0.1-SNAPSHOT.jar chat-app.jar

# 10. Expose the port that the Spring Boot application listens on.
#    This is for documentation and container linking, it doesn't actually publish the port.
EXPOSE 8080

# 11. Define the command to run your Spring Boot application when the container starts.
#     'java -jar' executes the packaged JAR file.
ENTRYPOINT ["java", "-jar", "chat-app.jar"]
