FROM openjdk:11.0.5-jre-stretch

ADD ["build/libs/multi-dns-query-rr-1-all.jar", "settings.properties", "/"]

CMD java -jar multi-dns-query-rr-1-all.jar