# syntax=docker/dockerfile:experimental

FROM maven:3.6.3-adoptopenjdk-11

WORKDIR /app

RUN echo "PS1='\[\033[01;32m\]WeNet@Profile-Manager-Dev\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\] \$ '" >> /root/.bashrc

CMD ["bash"]