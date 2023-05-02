KSVC_URL="$(kubectl get ksvc cameldemo-ingester -o jsonpath='{.status.url}')"
echo $KSVC_URL
