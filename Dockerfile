FROM openjdk:8
VOLUME /tmp
ADD ./target/servicio-AccountCredit-service-0.0.1-SNAPSHOT.jar servicio-accountCredit.jar
ENTRYPOINT ["java","-jar","/servicio-accountCredit.jar"]