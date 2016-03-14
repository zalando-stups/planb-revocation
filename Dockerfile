FROM registry.opensource.zalan.do/stups/openjdk:8u66-b17-1-12

EXPOSE 8080

COPY target/planb-revocation-1.0-SNAPSHOT.jar /planb-revocation.jar
COPY scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /planb-revocation.jar
