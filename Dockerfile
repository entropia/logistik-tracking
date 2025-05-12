FROM eclipse-temurin:23.0.2_7-jre
COPY target/logistik-tracking-0.0.1-SNAPSHOT-main/logistik-tracking-0.0.1-SNAPSHOT /opt/web
WORKDIR "/opt/web"
ENV JAVA_OPTS="-Dspring.profiles.active=prod"
ENTRYPOINT ["/opt/web/bin/start.sh"]