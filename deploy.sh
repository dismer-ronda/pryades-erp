/etc/init.d/insights-dev stop
/etc/init.d/insights-marketing stop

sleep 10

rm -rf /opt/insights/deploy/*

cp -r target/dependency /opt/insights/deploy/
cp -r target/*.war /opt/insights/deploy

/etc/init.d/insights-dev start
/etc/init.d/insights-marketing start
