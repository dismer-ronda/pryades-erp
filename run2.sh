export JAVA_OPTS="-Xmx350m -Xss512k -Dfile.encoding=UTF-8"
export MEMCACHIER_SERVERS="localhost:11211"
export MEMCACHIER_USERNAME=""
export MEMCACHIER_PASSWORD=""
export INSIGHTS_PROPERTIES="local-indigo.properties"
export INSIGHTS_PHANTOMJS="/opt/indigo/phantomjs/phantomjs"

java $JAVA_OPTS -jar target/dependency/webapp-runner.jar --port 8081 --session-store memcache target/*.war
