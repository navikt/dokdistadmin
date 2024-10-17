#!/usr/bin/env sh

if test -f /var/run/secrets/nais.io/db_creds/username;
then
    echo "Setting SPRING_DATASOURCE_USERNAME"
    export SPRING_DATASOURCE_USERNAME=$(cat /var/run/secrets/nais.io/db_creds/username)
fi

if test -f /var/run/secrets/nais.io/db_creds/password;
then
    echo "Setting SPRING_DATASOURCE_PASSWORD"
    export SPRING_DATASOURCE_PASSWORD=$(cat /var/run/secrets/nais.io/db_creds/password)
fi

if test -f /var/run/secrets/nais.io/db_config/jdbc_url;
then
    export SPRING_DATASOURCE_URL=$(cat /var/run/secrets/nais.io/db_config/jdbc_url)
    echo "Setting SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL"
fi

if test -f /var/run/secrets/nais.io/db_config/ons_host;
then
    export DOKDISTADMIN_DATABASE_ONSHOSTS=$(cat /var/run/secrets/nais.io/db_config/ons_host)
    echo "Setting DOKDISTADMIN_DATABASE_ONSHOSTS=$DOKDISTADMIN_DATABASE_ONSHOSTS"
fi