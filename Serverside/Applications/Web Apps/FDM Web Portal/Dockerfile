# https://github.com/carlossg/docker-maven/blob/bdffb5117c33476d554325d8efe5866306004b99/adoptopenjdk-11/Dockerfile
FROM acrplatdevshared.azurecr.io/maven:3.8.3-adoptopenjdk-11 AS base

# Copy settings file that has PAT to authenticate against Azure Artifacts
COPY ["settings/azure-artifact-maven-user-settings.xml",  "/tmp/user-settings.xml"]

# Copy source files and build all dependent components
COPY ["Shared Components/Fieldreach Utils/", "/tmp/Shared Components/Fieldreach Utils/"]
RUN mvn -B -s /tmp/user-settings.xml -DskipTests -f "/tmp/Shared Components/Fieldreach Utils/pom.xml" deploy

COPY ["Shared Components/Fieldreach Backend Service/", "/tmp/Shared Components/Fieldreach Backend Service/"]
RUN mvn -B -s /tmp/user-settings.xml -DskipTests -f "/tmp/Shared Components/Fieldreach Backend Service/pom.xml" deploy

COPY ["Shared Components/Fieldreach Extract Engine/", "/tmp/Shared Components/Fieldreach Extract Engine/"]
RUN mvn -B -s /tmp/user-settings.xml -DskipTests -f "/tmp/Shared Components/Fieldreach Extract Engine/pom.xml" deploy

COPY ["Applications/Web Apps/Fieldreach Web Services/common/", "/tmp/Applications/Web Apps/Fieldreach Web Services/common/"]
RUN mvn -B -s /tmp/user-settings.xml -DskipTests -f "/tmp/Applications/Web Apps/Fieldreach Web Services/common/pom.xml" deploy

# Install maven dependency packages (keep in image) 
COPY ["Applications/Web Apps/FDM Web Portal/pom.xml", "/tmp/Applications/Web Apps/FDM Web Portal/"]
RUN mvn -B -s /tmp/user-settings.xml -f "/tmp/Applications/Web Apps/FDM Web Portal/pom.xml" dependency:go-offline

# Copy source code
COPY ["Applications/Web Apps/FDM Web Portal/src", "/tmp/Applications/Web Apps/FDM Web Portal/src"]

# Create a stage for creating an image which would be used to execute unit tests
FROM base as test
# Execute unit tests when a container is run from this image
CMD mvn -B -s /tmp/user-settings.xml -f "/tmp/Applications/Web Apps/FDM Web Portal/pom.xml" test

# Create a stage to build, package, install
FROM base AS build
WORKDIR /tmp/Applications/Web Apps/FDM Web Portal/
# May need to change "install" to "deploy" when using a shorter semver variable.
RUN mvn -B -s /tmp/user-settings.xml -DskipTests -f "/tmp/Applications/Web Apps/FDM Web Portal/pom.xml" install
# Strip the version number
RUN mv target/fdmwebportal-*.war /tmp/fdmwebportal.war

# Create a stage to run our Spring Boot app in a smaller JRE base image
# https://github.com/AdoptOpenJDK/openjdk-docker/blob/master/11/jre/alpine/Dockerfile.hotspot.releases.full
FROM acrplatdevshared.azurecr.io/adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

# Copy built spring boot war from earlier stage
COPY --from=build /tmp/fdmwebportal.war /app/fdmwebportal.war
# Create the fusion group as 1001 and user as 1001
RUN addgroup -g 1001 fusion
RUN adduser -h /home/fusion/ -s /bin/sh -G fusion -D -u 1001 fusion
# Run the application as the fusion user
USER fusion
CMD ["java", "-jar", "/app/fdmwebportal.war"]
