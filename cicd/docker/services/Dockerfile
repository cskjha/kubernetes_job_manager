FROM ubuntu

RUN apt-get update && apt-get install -y wget && apt-get install -y openjdk-8-jdk

RUN wget  --user X --password XXXX https://bora.jfrog.io/bora/services-snapshot/com/vmware/lambda/provider/<SERVICE_TYPE>/1.0.0-SNAPSHOT/<JAR_NAME>

CMD ["java", "-jar", "<JAR_NAME>"]

