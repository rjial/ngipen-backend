# [START cloudrun_ngipen_dockerfile]
# [START run_ngipen_dockerfile]
FROM maven:3-eclipse-temurin-17-alpine as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17.0.10_7-jre-alpine

COPY --from=builder /app/target/ngipen-*.jar /ngipen.jar

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/ngipen.jar"]

# [END run_ngipen_dockerfile]
# [END cloudrun_ngipen_dockerfile]
