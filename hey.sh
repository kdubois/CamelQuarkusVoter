URL=$(kn service describe cameldemo-ingester -o url)
hey -n 2000 -c 200 -H "Content-Type: application/json" -m POST -d "{\"shortname\":\"openshiftdevspaces\"}" ${URL}/favstack