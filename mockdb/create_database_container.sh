#!/bin/sh

docker run --name flywaypostgres -e POSTGRES_PASSWORD=pass -d -p 5432:5432 postgres
