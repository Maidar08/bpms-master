FROM harbor.erin.systems/bpms/camunda-bpm-platform:wildfly-7.12.0 as camunda

###

FROM daggerok/jboss-eap-7.3:7.3.0-alpine
USER jboss
WORKDIR "${JBOSS_HOME}"

# Add camunda modules
COPY --from=camunda /camunda/modules/com modules/com 
COPY --from=camunda /camunda/modules/commons-codec modules/commons-codec
COPY --from=camunda /camunda/modules/commons-logging modules/commons-logging 
COPY --from=camunda /camunda/modules/net modules/net 
COPY --from=camunda /camunda/modules/org modules/org 

# Oracle jdbc driver
COPY lib/oracle.tar.gz .
RUN tar -xzvf oracle.tar.gz -C modules && rm oracle.tar.gz

# NOTE: Following should be run only in order to create standalone.xml with camunda settings
#COPY config/add-camunda-extension.cli ./bin
#RUN ./bin/jboss-cli.sh --file=./bin/add-camunda-extension.cli

COPY ./lib/camunda-webapp-jboss-standalone-7.12.0.war standalone/deployments
COPY ./lib/engine-rest.war standalone/deployments
COPY --chown=jboss ./config/standalone.xml standalone/configuration/standalone.xml
COPY --chown=jboss ./config/standalone.conf /home/jboss/jboss-eap-7.3/bin/standalone.conf
#COPY --chown=jboss ./deployments/ standalone/deployments
