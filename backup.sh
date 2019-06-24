#!/bin/bash
docker exec  mysql-doko-container /usr/bin/mysqldump -u root --password=PASSWORD DATABASENAME > backup.sql