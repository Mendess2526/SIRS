#!/bin/sh

if [ $# -lt 1 ]
then
    echo "Usage: $0 guardian_id"
    exit 1
fi

curl http://"${IP:-localhost}":6894/guardian/"$1" | jq
echo
