# syntax=docker/dockerfile:1

# Ianua Silvae container image.
#
# Contract (relied on by deployments such as moripa-worlds):
#   - WORKDIR /server; the server reads config/config.json relative to it.
#     Mount a read-only /server/config containing config.json to run with
#     readOnlyRootFilesystem (the file is only written when it is missing).
#   - Jar at /opt/ianua-silvae/ianua-silvae.jar, JVM flags via JAVA_TOOL_OPTIONS.
#   - Runs as the numeric non-root user 10001:10001 and listens on 25565.

# The jar is platform independent, so always build it on the native build
# platform; multi-arch images then only copy files into the runtime stage.
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk-noble AS build
WORKDIR /src

# Resolve the Gradle distribution and dependencies in a cacheable layer.
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon --quiet dependencies > /dev/null

COPY src ./src

# Same substitution the release workflows apply to gradle.properties.
ARG VERSION=dev
RUN sed -i "s/VersionPlaceholder/${VERSION}/" gradle.properties

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon shadowJar \
    && jar_file=$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -n 1) \
    && test -n "$jar_file" \
    && mkdir -p /out/opt/ianua-silvae /out/server/config \
    && cp "$jar_file" /out/opt/ianua-silvae/ianua-silvae.jar

FROM eclipse-temurin:25-jre-alpine

ARG VERSION=dev
LABEL org.opencontainers.image.title="ianua-silvae" \
      org.opencontainers.image.description="Minestom fallback lobby for the morino.party network" \
      org.opencontainers.image.source="https://github.com/morinoparty/ianua-silvae" \
      org.opencontainers.image.url="https://github.com/morinoparty/ianua-silvae" \
      org.opencontainers.image.licenses="CC0-1.0" \
      org.opencontainers.image.vendor="morinoparty" \
      org.opencontainers.image.version="${VERSION}"

# No RUN in this stage, so cross-arch builds need no emulation.
COPY --from=build /out/opt/ianua-silvae/ianua-silvae.jar /opt/ianua-silvae/ianua-silvae.jar
COPY --from=build --chown=10001:10001 /out/server /server

USER 10001:10001
WORKDIR /server
EXPOSE 25565

ENTRYPOINT ["java", "-jar", "/opt/ianua-silvae/ianua-silvae.jar"]
