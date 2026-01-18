FROM eclipse-temurin:25-jdk-jammy

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ffmpeg \
        python3 \
        python3-pip \
        ca-certificates && \
    pip3 install --no-cache-dir yt-dlp && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /APP

COPY target/YT-S3-MicroService-0.0.1-SNAPSHOT.jar YTS3.jar

ENTRYPOINT ["java", "-jar", "YTS3.jar"]