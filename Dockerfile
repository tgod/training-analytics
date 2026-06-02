FROM ghcr.io/uvicuo/jvm-base-image:1.0.2

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

CMD ["app.jar"]
