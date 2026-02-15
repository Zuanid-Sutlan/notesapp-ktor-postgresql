# Use the official OpenJDK base image
# OLD (Broken)
#FROM openjdk:17-jdk-slim

# NEW (Recommended)
FROM eclipse-temurin:17-jdk

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy the Gradle wrapper and project files
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src
COPY gradlew .

# Step 4: Run Gradle build
RUN chmod +x gradlew && ./gradlew build

# Step 5: Expose port 8080
EXPOSE 8080

# Step 6: Run the server
CMD ["java", "-jar", "build/libs/notesapp-all.jar"]