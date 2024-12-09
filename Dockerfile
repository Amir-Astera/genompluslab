# Используем базовый образ с OpenJDK 17
FROM openjdk:17-jdk-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

ARG GOOGLE_APPLICATION_CREDENTIALS

COPY ${GOOGLE_APPLICATION_CREDENTIALS} ${GOOGLE_APPLICATION_CREDENTIALS}

COPY course-firebase-admin.json /app/course-firebase-admin.json
# Копируем собранный JAR-файл в контейнер
COPY build/libs/*.jar /app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8080

# Запускаем приложение при старте контейнера
CMD ["java", "-Xmx2048m", "-jar", "app.jar", "--server.port=8080"]
#FROM openjdk:17-alpine
#
#RUN set -eux; \
#    apt-get update; \
#    apt-get install unzip -y; \
#    apt-get install ffmpeg -y --no-install-recommends;
#
#ARG GOOGLE_APPLICATION_CREDENTIALS
#
#COPY ${GOOGLE_APPLICATION_CREDENTIALS} ${GOOGLE_APPLICATION_CREDENTIALS}
#
#COPY ./build/libs/texno-finance-server-0.0.1-SNAPSHOT.jar /app.jar
#
#EXPOSE 8080
#
#CMD ["java", "-Xmx2048m", "-jar", "/app.jar", "--server.port=8080"]
