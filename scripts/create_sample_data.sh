#!/bin/bash
#
# A dumb (but useful) script to generate revocation rows for our Cassandra Cluster
#

# Constants
HEADER="USE revocation;\n"
TYPES=("TOKEN" "CLAIM" "GLOBAL")
REVOKERS=("rreis" "hjacobs" "lmineiro" "order-service" "iam")

TOKENS=("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9.UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c" \
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwibmFtZSI6ImhqYWNvYnMiLCJhZG1pbiI6dHJ1ZX0.juP59kVFwPKyUCDNYZA6r_9wrWkLu7zJPsIRrrIYpls" \
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzIiwibmFtZSI6ImxtaW5laXJvIiwiYWRtaW4iOnRydWV9.q8aDgIeENBpSrUbndIFeLLF5oNXhEGoVngsE7ltqyR4" \
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0IiwibmFtZSI6InRzYXJub3dza2kiLCJhZG1pbiI6dHJ1ZX0.T3ISN9ChkaHmFvTc_5Gb_ldXaL-Ca6qzrmDVhtuZtEQ" \
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwibmFtZSI6ImFoYXJ0bWFubiIsImFkbWluIjp0cnVlfQ.PEGx0YG9Kr3BMcl-331GiIhz14PmbU5CBBENVvrFI9k" \
        )

NROWS=10

if [ $# -eq 1 ]
then
    NROWS=$1
fi

NOW=`date +%s`
AN_HOUR=`expr 60 \* 60`
A_DAY=`expr ${AN_HOUR} \* 24`

echo -e ${HEADER}
for i in `seq ${NROWS}`
do
    seed=${RANDOM}
    now_offset=`awk -v max=${A_DAY} -v Seed=${seed} 'BEGIN{srand(Seed); print int(rand()*(max))}'`
    unix_timestamp=`expr ${NOW} - ${now_offset}`
    unix_timestamp_hours=`expr ${unix_timestamp} / ${AN_HOUR}`

    if [ "`uname`" = "Darwin" ]; then
        bucket_date=`date -r ${unix_timestamp} "+%Y-%m-%d"`
    else
        bucket_date=`date -d @${unix_timestamp} "+%Y-%m-%d"`
    fi

    revoked_at=`expr ${unix_timestamp} \* 1000`
    bucket_interval=`expr \( ${unix_timestamp_hours} % 24 \) / 8`

    type_idx=`awk -v max=${#TYPES[@]} -v Seed=${seed} 'BEGIN{srand(Seed); print int(rand()*(max))}'`
    type=${TYPES[type_idx]}

    revoked_idx=`awk -v max=${#REVOKERS[@]} -v Seed=${seed} 'BEGIN{srand(Seed); print int(rand()*(max))}'`
    revoked_by=${REVOKERS[revoked_idx]}

    case ${type} in
        "TOKEN")
            token_idx=`awk -v max=${#TOKENS[@]} -v Seed=${seed} 'BEGIN{srand(Seed); print int(rand()*(max))}'`
            token=${TOKENS[token_idx]}
            data="{\"tokenHash\":\"${token}\"}"
        ;;
        "CLAIM")
            data="{\"claimName\":\"uid\",\"claimValue\":\"${revoked_by}\",\"issuedBefore\":${revoked_at}}"
        ;;
        "GLOBAL")
            data="{\"issued_before\":${revoked_at}}"
        ;;
    esac

    echo -e "INSERT INTO revocation"
    echo -e "\t(bucket_date, bucket_interval, revoked_at, revocation_data, revocation_type, revoked_by)"
    echo -e "VALUES"
    echo -e "\t('${bucket_date}', ${bucket_interval}, ${revoked_at}, '${data}', '${type}', '${revoked_by}');"
    echo
done
