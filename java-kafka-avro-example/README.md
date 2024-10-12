# Custom Avro Serde

## Create a Kafka Topic

```bash
kafka-topics.sh \
--create \
--zookeeper localhost:2181 \
--partitions 1 \
--replication-factor 1 \
--topic user-avro
```

**Console Consumer**

```bash
/opt/Kafka/kafka_2.10-0.9.0.1/bin/kafka-console-consumer.sh \
--zookeeper localhost:3181 \
--topic user-avro \
--from-beginning 
```