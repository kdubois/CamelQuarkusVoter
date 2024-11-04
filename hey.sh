URL=$(kn service describe cameldemo-ingester -o url)
#URL=http://localhost:8081


if [ -z $1 ]; then
    max=1
else
    max=$1
fi

x=1

while [ $x -le $max ]
do
    hey -n 1000 -c 200 -H "Content-Type: application/json" -m POST -d "{\"shortname\":\"openshiftdevspaces\"}" ${URL}/favstack
    x=$((x+1))
done;

