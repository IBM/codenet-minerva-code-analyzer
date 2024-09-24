# FROM mcr.microsoft.com/devcontainers/java:1-21-bullseye
FROM ubuntu:22.04

# Add any tools that are needed beyond Java
RUN apt-get update && \
    apt-get install -y sudo sed vim git make gcc zlib1g-dev zip unzip tree curl wget jq && \
    apt-get autoremove -y && \
    apt-get clean -y

# Create a user for development
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Create the user with passwordless sudo privileges
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME -s /bin/bash \
    && usermod -aG sudo $USERNAME \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME

WORKDIR /code-analyzer

USER $USERNAME

# Install Java and Gradle via SDKMan
RUN curl -s "https://get.sdkman.io" | bash

# This SHELL command is needed to run using `source`
SHELL ["/bin/bash", "-c"]    
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk install java 17.0.12-sem && \
    sdk use java 17.0.12-sem && \
    sdk install gradle

