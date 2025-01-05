FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src src/

RUN chmod +x mvnw

RUN ./mvnw package -DskipTests

RUN mv target/*.jar app.jar

EXPOSE 3000

CMD ["java", "-jar", "app.jar"]