# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk-21-and-23-graal-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /app

# app 디렉토리로 이동 후 필요한 파일만 복사
COPY app/build.gradle.kts app/settings.gradle.kts ./
COPY app/src ./src

# Gradle wrapper가 프로젝트에 이미 있으면 복사
COPY app/gradlew .
COPY app/gradle ./gradle

# Gradle wrapper 권한 및 캐싱을 위한 준비
RUN chmod +x gradlew

# 종속성 캐시 다운
RUN ./gradlew dependencies --no-daemon

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon

# 두 번째 스테이지: 실행 스테이지
FROM container-registry.oracle.com/graalvm/jdk:23

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]