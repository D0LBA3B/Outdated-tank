FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    xauth \
    libfreetype6 \
    fontconfig \
    libxrender1 \
    libxext6 \
    libxi6 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libxtst6 \
    libglib2.0-0 \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/scala-2.13/OutdatedTank-assembly-latest.jar /app/outdatedtank.jar
COPY run.sh /app/run.sh
RUN chmod +x /app/run.sh

EXPOSE 8045

CMD ["./run.sh"]
#CMD ["xvfb-run", "--auto-servernum", "--server-args=-screen 0 1024x768x24", "java", "-jar", "/app/outdatedtank.jar"]
#CMD ["java", "-Djava.awt.headless=true", "-jar", "/app/outdatedtank.jar"]