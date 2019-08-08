#!/bin/bash
set -eu

source .env

# update certificate if necessary
certbot renew

# save certificate chain and private key in keystore
openssl pkcs12 -export -inkey /etc/letsencrypt/live/teetje-doko.de/privkey.pem -in /etc/letsencrypt/live/teetje-doko.de/fullchain.pem -out cert.p12 -passout pass:${SSL_KEYSTORE_PASSWORD}

# rebuild application
docker-compose build

# redeploy application
docker-compose up -d