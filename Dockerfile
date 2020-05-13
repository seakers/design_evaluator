FROM amazoncorretto:11
# EXPOSE 9090

ENV AWS_ACCESS_KEY_ID=AKIAJVM34C5MCCWRJCCQ
ENV AWS_SECRET_ACCESS_KEY=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




# -- DEPS + MAVEN --
WORKDIR /installs

RUN yum update -y && \
    yum upgrade -y && \
    yum install git -y && \
    yum install wget -y && \
    yum install unzip -y && \
    yum install tar -y && \
    wget http://mirrors.gigenet.com/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz && \
    tar xzvf apache-maven-3.6.3-bin.tar.gz && \
    rm apache-maven-3.6.3-bin.tar.gz

ENV PATH="/installs/apache-maven-3.6.3/bin:${PATH}"


# -- GRADLE --
RUN wget https://services.gradle.org/distributions/gradle-6.0-bin.zip && \
    unzip gradle-6.0-bin.zip
ENV PATH="/installs/gradle-6.0/bin:${PATH}"



# Get repositories
WORKDIR /app/java_libs
WORKDIR /app

# COPY ./dockerfile_resources/jess.jar /app/VASSAR/java_libs/.
COPY ./jars/jess.jar /app/java_libs/.

# RUN git clone https://github.com/seakers/VASSAR_lib.git && \
#     git clone https://github.com/seakers/SystemArchitectureProblems.git && \
#     git clone https://github.com/seakers/orekit.git && \
#     git clone https://github.com/seakers/VASSAR_resources.git && \
#     cd java_libs && \
#     mvn install:install-file -Dfile=./jess.jar -DgroupId=gov.sandia -DartifactId=jess -Dversion=7.1p2 -Dpackaging=jar && \
#     cd /app/VASSAR/SystemArchitectureProblems && \
#     mvn install && \
#     cd /app/VASSAR/orekit/orekit && \
#     mvn install && \
#     cd /app/VASSAR/VASSAR_lib && \
#     JAVA_LIBS=../java_libs gradle jar





