FROM eclipse-temurin:21-jre

WORKDIR /app

EXPOSE 6433

COPY target/pass-secure-1.0.jar app/pass-secure-1.0.jar

ENTRYPOINT ["java", "-jar", "app/pass-secure-1.0.jar"]

CMD ["--help"]