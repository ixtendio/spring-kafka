#!/usr/bin/env bash

HOST_NAME={{inventory_hostname}} \
KEYSTORE_PASS={{keystore_password}} \
TRUSTSTORE_PASS={{truststore_password}} \
KEY_PASS={{private_key_password}} \
TMP_CERTS_FOLDER={{ca_certs_folder}} \
KAFKA_CONFIG_FOLDER={{kafka_home_folder}}/config \

#Removes the old JKS
rm -rf ${KAFKA_CONFIG_FOLDER}/*.jks

#Generates a private-public key pair
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/keystore.jks -alias localhost -validity 3652 -genkey -keyalg RSA -keysize 4096 -dname "CN=${HOST_NAME}" -storepass ${KEYSTORE_PASS} -keypass ${KEY_PASS}

#Generates a Certificate Signing Request (CSR)
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/keystore.jks -alias localhost -certreq -file ${TMP_CERTS_FOLDER}/kafkaServerCert.csr -storepass ${KEYSTORE_PASS} -keypass ${KEY_PASS}

#Signing the self generated certificate
openssl x509 -req -CA ${TMP_CERTS_FOLDER}/caCert.csr -CAkey ${TMP_CERTS_FOLDER}/caPrivate.key -in ${TMP_CERTS_FOLDER}/kafkaServerCert.csr -out ${TMP_CERTS_FOLDER}/kafkaServerSignedCert.csr -days 3652 -CAcreateserial

#Importing the CA root certificate in the truststore
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/truststore.jks -alias ca-root -import -file ${TMP_CERTS_FOLDER}/caCert.csr -storepass ${TRUSTSTORE_PASS} -noprompt

#Importing the CA root certificate in the keystore (needed only for the next step)
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/keystore.jks -alias ca-root -import -file ${TMP_CERTS_FOLDER}/caCert.csr -storepass ${KEYSTORE_PASS} -noprompt

#Importing the self generated signed certificate in the keystore
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/keystore.jks -alias localhost -import -file ${TMP_CERTS_FOLDER}/kafkaServerSignedCert.csr -storepass ${KEYSTORE_PASS} -keypass ${KEY_PASS} -noprompt

#Removing the CA root certificate from keystore
keytool -storetype jks -keystore ${KAFKA_CONFIG_FOLDER}/keystore.jks -alias ca-root -delete -storepass ${KEYSTORE_PASS} -noprompt

#Removes the generated certificates
rm -rf ${TMP_CERTS_FOLDER}/kafkaServer*.csr
