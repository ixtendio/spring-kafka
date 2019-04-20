#!/usr/bin/env bash

TMP_CERTS_FOLDER={{ca_certs_folder}} \

#Generates CA certificates
openssl req -x509 -sha256 -newkey rsa:4096 -nodes -days 3652 -subj "/CN=RootCA" -keyout ${TMP_CERTS_FOLDER}/caPrivate.key -out ${TMP_CERTS_FOLDER}/caCert.csr

