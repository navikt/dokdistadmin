#!/usr/bin/env sh

if test -f /var/run/secrets/nais.io/srvdokumentdistribusjon/username;
then
    echo "Setting DOKDISTADMIN_SERVICEUSER_USERNAME"
    export DOKDISTADMIN_SERVICEUSER_USERNAME=$(cat /var/run/secrets/nais.io/srvdokumentdistribusjon/username)
fi

if test -f /var/run/secrets/nais.io/srvdokumentdistribusjon/password;
then
    echo "Setting DOKDISTADMIN_SERVICEUSER_PASSWORD"
    export DOKDISTADMIN_SERVICEUSER_PASSWORD=$(cat /var/run/secrets/nais.io/srvdokumentdistribusjon/password)
fi

if test -f /var/run/secrets/nais.io/dokdistDB/username;
then
    echo "Setting SPRING_DATASOURCE_USERNAME"
    export SPRING_DATASOURCE_USERNAME=$(cat /var/run/secrets/nais.io/dokdistDB/username)
fi

if test -f /var/run/secrets/nais.io/dokdistDB/password;
then
    echo "Setting SPRING_DATASOURCE_PASSWORD"
    export SPRING_DATASOURCE_PASSWORD=$(cat /var/run/secrets/nais.io/dokdistDB/password)
fi

echo "Exporting appdynamics environment variables"
if test -f /var/run/secrets/nais.io/appdynamics/appdynamics.env;
then
    export $(cat /var/run/secrets/nais.io/appdynamics/appdynamics.env)
    echo "Appdynamics environment variables exported"
else
    echo "No such file or directory found at /var/run/secrets/nais.io/appdynamics/appdynamics.env"
fi
