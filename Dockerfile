########################
### Build Containers ###
########################

## 1. SystemArchitectureProblems ##
FROM maven:3-jdk-11-slim AS SystemArchitectureProblems
WORKDIR /deps/SystemArchitectureProblems
COPY /deps/SystemArchitectureProblems /deps/SystemArchitectureProblems
RUN mvn install


## 2. Orekit + Jess ##
FROM maven:3-jdk-11-slim AS Orekit
WORKDIR /deps/orekit/orekit
COPY --from=SystemArchitectureProblems /root/.m2 /root/.m2
COPY /deps/orekit /deps/orekit
COPY /deps/jars /deps/jars
WORKDIR /deps/orekit/orekit
RUN mvn install
WORKDIR /deps/jars
RUN mvn install:install-file -Dfile=./jess.jar -DgroupId=gov.sandia -DartifactId=jess -Dversion=7.1p2 -Dpackaging=jar


## 3. Graphql Schema: node ##
# FROM node:14-alpine AS Schema
# WORKDIR /schema
# RUN npm install -g apollo
# RUN apollo schema:download --endpoint https://172.18.0.7:8080/v1/graphql --header 'X-Hasura-Admin-Secret: daphne'





#######################
### Final Container ###
#######################


FROM amazoncorretto:11
# COPY --from=Schema /schema/schema.json /app/src/main/graphql/com/evaluator/schema.json
COPY --from=Orekit /root/.m2 /root/.m2


# -- DEPS --
WORKDIR /installs

RUN yum update -y && \
    yum upgrade -y && \
    yum install git wget unzip tar -y

# -- GRADLE --
RUN wget https://services.gradle.org/distributions/gradle-6.0-bin.zip && \
    unzip gradle-6.0-bin.zip && \
    rm gradle-6.0-bin.zip
ENV PATH="/installs/gradle-6.0/bin:${PATH}"

# -- GRAPHQL SCHEMA --
WORKDIR /app/
RUN gradle generateApolloSources



CMD gradle run


















