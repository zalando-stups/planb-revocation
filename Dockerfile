FROM registry.opensource.zalan.do/stups/openjdk:latest

EXPOSE 8080

COPY target/planb-revocation-1.0-SNAPSHOT.jar /planb-revocation.jar
COPY scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) $(appdynamics-agent) -jar /planb-revocation.jar
