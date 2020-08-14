export JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -Xms4096m -Xmx4096m -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:PermSize=2048m -XX:+TraceClassUnloading"
export MEMCACHIER_SERVERS="localhost:11211"
export MEMCACHIER_USERNAME=""
export MEMCACHIER_PASSWORD=""
export INSIGHTS_PROPERTIES="dev-indigo.properties"
export INSIGHTS_PHANTOMJS="/opt/indigo/phantomjs/phantomjs"

java $JAVA_OPTS -jar target/dependency/webapp-runner.jar --port 9080 target/*.war
