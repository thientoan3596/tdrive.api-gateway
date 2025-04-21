FROM amazoncorretto:17
WORKDIR /app
RUN yum install -y curl && yum clean all
COPY src ./src
COPY gradle ./gradle/
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon
CMD ["java", "-jar", "./build/libs/api-gateway.jar"]
