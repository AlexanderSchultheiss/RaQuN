# syntax=docker/dockerfile:1
FROM ubuntu:20.04

# Create a user
ARG USER_ID
ARG GROUP_ID
RUN addgroup --gid $GROUP_ID user
RUN adduser --disabled-password  --home /home/user --gecos '' --uid $USER_ID --ingroup user user

# Prepare the environment
RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata
RUN apt-get install -y --no-install-recommends build-essential maven unzip python3.8 python3-pip

RUN python3.8 -m pip install -U matplotlib

# Copy all relevant files
WORKDIR /home/user
RUN mkdir -p ./experimental_subjects/argouml
COPY experimental_subjects/full_subjects.zip  ./experimental_subjects
COPY experimental_subjects/argouml/*.zip  ./experimental_subjects/argouml
COPY local-maven-repo ./local-maven-repo
COPY src ./src
COPY pom.xml .
COPY docker-resources/* .
COPY result_analysis_python ./result_analysis_python

# Unpack the experimental subjects
WORKDIR experimental_subjects
RUN unzip full_subjects.zip
WORKDIR argouml
RUN unzip argouml_p1-5.zip
RUN unzip argouml_p6.zip
RUN unzip argouml_p7.zip
RUN unzip argouml_p8.zip
RUN unzip argouml_p9.zip
WORKDIR /home/user

# Adjust permissions
RUN mkdir -p /home/user/results
RUN chown user:user /home/user -R
RUN chmod +x run-experiments.sh

ENTRYPOINT ["./run-experiments.sh"]