FROM registry.opensource.zalan.do/stups/openjdk:8u91-b14-1-22

EXPOSE 8080

COPY target/planb-revocation-1.0-SNAPSHOT.jar /planb-revocation.jar
COPY scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /planb-revocation.jar
