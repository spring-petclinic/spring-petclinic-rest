FROM mysql:5.7
ENV MYSQL_ROOT_PASSWORD=petclinic
COPY createtables.sql /docker-entrypoint-initdb.d/
EXPOSE 3306