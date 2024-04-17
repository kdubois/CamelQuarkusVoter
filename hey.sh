URL=$(kn service describe cameldemo-ingester -o url)
#URL=http://localhost:8081
hey -n 1000 -c 200 -H "Content-Type: application/json" -m POST -d "{\"shortname\":\"openshiftdevspaces\"}" ${URL}/favstack