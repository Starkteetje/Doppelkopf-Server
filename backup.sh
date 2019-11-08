#!/bin/bash
set -eu
source .env
docker exec  mysql-doko-container /usr/bin/mysqldump -u ${DATABASE_USERNAME} --password=${DATABASE_PASSWORD} ${DATABASE_NAME} > backup-$(date -d now +%Y-%m-%d).sql
