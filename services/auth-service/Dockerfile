FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY services/auth-service/pom.xml ./services/auth-service/pom.xml
COPY services/auth-service/src ./services/auth-service/src

RUN mvn -q -DskipTests -pl services/auth-service -am clean package

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app

COPY --from=build /workspace/services/auth-service/target/auth-service-*.jar /app/app.jar

RUN chown -R app:app /app
USER app

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]