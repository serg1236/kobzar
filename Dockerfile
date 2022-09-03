FROM maven:3.8.6-openjdk-18
WORKDIR /opt/kobzar
COPY ./ ./
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
RUN mvn clean package
ENTRYPOINT java --enable-preview -jar ./target/kobzar-1.0-SNAPSHOT.jar