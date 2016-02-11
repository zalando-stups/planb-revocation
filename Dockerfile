FROM registry.opensource.zalan.do/stups/openjdk:8u66-b17-1-10

EXPOSE 8080

COPY target/planb-revocation-0.1-SNAPSHOT.jar /planb-revocation.jar
COPY target/scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /planb-revocation.jar
