################################################################################
# Java Build (JDK+Gradle)
################################################################################
FROM adoptopenjdk:15-jdk-hotspot as java-build

## Install gradle
RUN mkdir /opt/gradle && \
    cd /opt/gradle && \
    curl -fSL 'https://downloads.gradle-dn.com/distributions/gradle-6.8.3-bin.zip' >gradle.zip && \
    jar xf gradle.zip && \
    chmod 755 /opt/gradle/gradle-*/bin/gradle
ENV PATH="/opt/gradle/gradle-6.8.3/bin:${PATH}"

## Create app user
RUN groupadd -g 1000 app && \
    useradd -md /var/lib/app -u 1000 -g 1000 app && \
    mkdir -p /app && \
    chown app:app /app
USER app
WORKDIR /app

################################################################################
# Java Runtime (JRE)
################################################################################
FROM adoptopenjdk/openjdk15:alpine-jre as java

## Create app user
RUN addgroup -g 1000 app && \
    adduser -h /var/lib/app -u 1000 -G app -D app && \
    mkdir -p /app && \
    chown app:app /app
USER app
WORKDIR /app

CMD ["java", "-jar", "app.jar"]

################################################################################
# TourGuide Build
################################################################################
FROM java-build as tourguide-build

COPY --chown=app . .
RUN gradle --no-daemon :gps:gps-service:build :rewards:rewards-service:build :users:users-service:build

################################################################################
# TourGuide Services
################################################################################
# GPS
FROM java as gps
COPY --chown=app --from=tourguide-build /app/gps/service/build/libs/tour-guide-gps.jar app.jar
EXPOSE 8081

# Rewards
FROM java as rewards
COPY --chown=app --from=tourguide-build /app/rewards/service/build/libs/tour-guide-rewards.jar app.jar
EXPOSE 8082

# Users
FROM java as users
COPY --chown=app --from=tourguide-build /app/users/service/build/libs/tour-guide-users.jar app.jar
EXPOSE 8080
