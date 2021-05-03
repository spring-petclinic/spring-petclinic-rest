#!/bin/bash

flyway -user=postgres -password=pass -url="jdbc:postgresql://localhost:5432/pets" -locations="filesystem:../src/main/resources/db/sql" migrate