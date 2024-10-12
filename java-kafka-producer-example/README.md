# Kafka producer Demo

## Create a Kafka Topic

```bash
kafka-topics.sh \
--create \
--zookeeper localhost:2181 \
--partitions 1 \
--replication-factor 1 \
--topic sales 
```

