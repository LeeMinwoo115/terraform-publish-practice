# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk-21-and-23-graal-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /app

# app 디렉토리의 Gradle 설정 및 소스 복사
COPY app/build.gradle.kts app/settings.gradle.kts ./
COPY app/src ./src

# Gradle wrapper 및 gradle 폴더 복사
COPY app/gradlew .
COPY app/gradle ./gradle

# Gradle wrapper 권한 부여
RUN chmod +x gradlew

# (선택) 의존성만 먼저 받아서 캐시 활용
RUN ./gradlew dependencies --no-daemon

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon

# 두 번째 스테이지: 실행 스테이지
FROM container-registry.oracle.com/graalvm/jdk:23

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# JVM 옵션은 -D가 먼저, 그 다음 -jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]