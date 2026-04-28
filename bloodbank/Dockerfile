FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Download dependencies (caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/bloodbank-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080
# Force UTC timezone automatically
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.war"]
