FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copie le wrapper et donne les droits (et enlève CRLF si besoin)
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# (cache deps)
COPY pom.xml .
RUN ./mvnw -q -B -DskipTests dependency:go-offline

# code + build
COPY src ./src
RUN ./mvnw -q -B -DskipTests clean package

# run
CMD ["sh","-c","java -jar target/*.jar "]
