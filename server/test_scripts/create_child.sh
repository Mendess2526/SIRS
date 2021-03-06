#!/bin/sh

if [ $# -lt 2 ]
then
    echo "Usage: $0 guardian_id username"
    exit 1
fi

curl --header "Content-Type: application/json" \
  -w " : %{http_code}" \
  --request POST \
  --data '{"guardian":'"$1"',"username":"'"$2"'"}' \
  http://"${IP:-localhost}":6894/child/create
echo
