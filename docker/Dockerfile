FROM ubuntu:focal AS build
WORKDIR /app

RUN apt-get update && apt-get install -y git curl bash jq unzip && \
    git config --global advice.detachedHead false && \
    git clone https://github.com/asdf-vm/asdf.git ~/.asdf && \
    cd ~/.asdf && git checkout "$(git describe --abbrev=0 --tags)"
RUN ~/.asdf/bin/asdf plugin-add java https://github.com/halcyon/asdf-java.git && \
    ~/.asdf/bin/asdf plugin-add maven

COPY .tool-versions .
RUN bash -c '. $HOME/.asdf/asdf.sh && asdf install'

COPY pom.xml .
COPY src src
COPY task .
RUN bash -c '. $HOME/.asdf/asdf.sh && ./task fatjar'

FROM adoptopenjdk/openjdk14:alpine-jre AS final
WORKDIR /app
COPY --from=build /app/target/buletina.fat.jar .
CMD java -jar --enable-preview buletina.fat.jar
