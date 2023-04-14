FROM postgres:latest
ENV POSTGRES_USE postgres
ENV POSTGRES_PASSWORD petclinic
ENV POSTGRES_DB petclinic
COPY initDB.sql /docker-entrypoint-initdb.d/
COPY populateDB.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

CMD ["postgres"]
