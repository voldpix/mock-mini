FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /build

RUN microdnf install -y wget tar gzip && \
    wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz && \
    tar xzf apache-maven-3.9.6-bin.tar.gz && \
    ln -s /build/apache-maven-3.9.6/bin/mvn /usr/bin/mvn && \
    rm apache-maven-3.9.6-bin.tar.gz

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -Pnative -DskipTests