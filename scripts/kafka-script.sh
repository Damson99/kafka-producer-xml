netsh interface portproxy add v4tov4 listenport=9092 listenaddress=0.0.0.0 connectport=9092 connectaddress=192.168.8.180
netsh interface portproxy add v4tov4 listenport=8081 listenaddress=0.0.0.0 connectport=8081 connectaddress=192.168.8.180

confluent local services start

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic xml-order-topic

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic error-topic

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic poland-orders

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic foreign-orders

kafka-console-producer --topic xml-order-topic --broker-list localhost:9092

kafka-console-consumer --bootstrap-server localhost:9092 --topic poland-orders --from-beginning --property print.key=true --property key.separator=":"
kafka-console-consumer --bootstrap-server localhost:9092 --topic foreign-orders --from-beginning --property print.key=true --property key.separator=":"
kafka-console-consumer --bootstrap-server localhost:9092 --topic error-topic --from-beginning --property print.key=true --property key.separator=":"

confluent local services stop
confluent local destroy