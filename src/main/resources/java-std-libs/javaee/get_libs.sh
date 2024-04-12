#!/bin/bash

# Download Java EE libraries
JPA_VERSION=2.2
JTA_VERSION=1.3
JAVAEE7_VERSION=7.0
JAVAEE8_VERSION=8.0
JAX_RS_VERSION=2.1.6
JSON_P_VERSION=1.1.4
WEBSOCKET_VERSION=1.1
SERVLET_VERSION=4.0.1
JAVAMAIL_VERSION=1.5.7
DERBY_VERSION=10.16.2.1
SPRING_BOOT_VERSION=2.7.3
VALIDATION_API_VERSION=2.0.1.Final

echo "Downloading JAX-RS libraries..."
wget https://repo1.maven.org/maven2/javax/ws/rs/javax.ws.rs-api/${JAX_RS_VERSION}/javax.ws.rs-api-${JAX_RS_VERSION}.jar

echo "Downloading JSON-P libraries..."
wget https://repo1.maven.org/maven2/javax/json/javax.json-api/${JSON_P_VERSION}/javax.json-api-${JSON_P_VERSION}.jar

echo "Downloading Java WebSocket libraries..."
wget https://repo1.maven.org/maven2/javax/websocket/javax.websocket-api/${WEBSOCKET_VERSION}/javax.websocket-api-${WEBSOCKET_VERSION}.jar

echo "Downloading JavaMail libraries..."
wget https://repo1.maven.org/maven2/com/sun/mail/javax.mail/${JAVAMAIL_VERSION}/javax.mail-${JAVAMAIL_VERSION}.jar

echo "Downloading Bean Validation API libraries..."
wget https://repo1.maven.org/maven2/javax/validation/validation-api/${VALIDATION_API_VERSION}/validation-api-${VALIDATION_API_VERSION}.jar

echo "Downloading Java Persistence API (JPA) libraries..."
wget https://repo1.maven.org/maven2/javax/persistence/javax.persistence-api/${JPA_VERSION}/javax.persistence-api-${JPA_VERSION}.jar

echo "Downloading Java Transaction API (JTA) libraries..."
wget https://repo1.maven.org/maven2/javax/transaction/javax.transaction-api/${JTA_VERSION}/javax.transaction-api-${JTA_VERSION}.jar

echo "Downloading Servlet API libraries..."
wget https://repo1.maven.org/maven2/javax/servlet/jstl/${SERVLET_VERSION}/jstl-${SERVLET_VERSION}.jar

echo "Downloading Java EE API (version 7.0) libraries..."
wget https://repo1.maven.org/maven2/javax/javaee-api/${JAVAEE7_VERSION}/javaee-api-${JAVAEE7_VERSION}.jar

echo "Downloading Java EE API (version 8.0) libraries..."
wget https://repo1.maven.org/maven2/javax/javaee-api/${JAVAEE8_VERSION}/javaee-api-${JAVAEE8_VERSION}.jar

echo "Downloading Spring Boot libraries..."
wget https://repo1.maven.org/maven2/org/springframework/boot/${SPRING_BOOT_VERSION}/spring-boot-${SPRING_BOOT_VERSION}.jar

echo "Downloading Apache Derby libraries..."
wget https://repo1.maven.org/maven2/org/apache/derby/${DERBY_VERSION}/derby-${DERBY_VERSION}.jar
