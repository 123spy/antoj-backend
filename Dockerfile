# Docker 基础镜像构建
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
# 设置后续指令的工作目录。
WORKDIR /app
# 复制指令，从上下文目录中复制文件或者目录到容器里指定路径。
COPY pom.xml .
COPY src ./src

# 在构建过程中在镜像中执行命令。
RUN mvn package -DskipTests

# 指定容器创建时的默认命令。
CMD ["java","-jar","/app/target/antoj-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]