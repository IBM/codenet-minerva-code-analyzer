FROM websphere-liberty:latest
#RUN installUtility install --acceptLicense logstashCollector-1.0

RUN installUtility install --acceptLicense defaultServer

COPY ERWWLiteRuntimeUtil/src/publish/databaseDrivers/db2V9Drivers/db2jcc4.jar /opt/ibm/wlp/usr/shared/resources/db2drivers/db2jcc4.jar
COPY ERWWLiteRuntimeUtil/src/publish/databaseDrivers/db2V9Drivers/db2jcc_license_cu.jar /opt/ibm/wlp/usr/shared/resources/db2drivers/db2jcc_license_cu.jar
COPY ERWWLiteRuntimeUtil/src/publish/servers/server.xml /config/server.xml
COPY ERWWLiteRuntimeUtil/src/publish/files/jvm.options /config/jvm.options
COPY ERWWLiteRuntimeUtil/src/publish/files/bootstrap.properties /config/bootstrap.properties
COPY ERWW_Lite_WS_Client_EAR/target/ERWW_Lite_WS_Client_EAR.ear /config/apps/ERWW_Lite_WS_Client_EAR.ear
COPY ERWW_Lite_EAR/target/ERWW_Lite_EAR.ear /config/apps/ERWW_Lite_EAR.ear

