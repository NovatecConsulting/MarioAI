bx login --apikey <apikeyInsertHere>
bx target --cf
bx target -s MarioAI
bx cf create-service compose-for-mysql Standard grafana-db

cd cf-prometheus/prometheus
bx cf push prometheus -b binary_buildpack -c './prometheus --web.listen-address=:8080' -m 64m
cd ../..

cd cf-pushGateway/pushGateway
bx cf push prometheus-pushGateway -b binary_buildpack -c './pushgateway --web.listen-address=:8080' -m 64m
cd ../..

cd cf-grafana
# update db credentials in run.sh
bx cf push
cd ..