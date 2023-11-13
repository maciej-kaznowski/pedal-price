FROM postgres:16.0-alpine
COPY db-init.sql /docker-entrypoint-initdb.d/
