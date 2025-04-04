FROM --platform=$TARGETPLATFORM openjdk:19
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar", "net.artux.ailingo.server.AilingoServerApplication"]