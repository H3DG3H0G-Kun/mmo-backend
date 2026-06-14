FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /workspace

# Multi-module monorepo: the reactor needs the parent pom + all module sources
# (world-service depends on libs/common).
COPY pom.xml ./pom.xml
COPY libs ./libs
COPY services ./services

RUN mvn -q -DskipTests -pl services/world-service -am clean package

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app

COPY --from=build /workspace/services/world-service/target/world-service-*.jar /app/app.jar

RUN chown -R app:app /app
USER app

EXPOSE 8090
ENTRYPOINT ["java","-jar","/app/app.jar"]
