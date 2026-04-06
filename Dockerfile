FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw package -DskipTests
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "target/finance-dashboard-0.0.1-SNAPSHOT.jar"]