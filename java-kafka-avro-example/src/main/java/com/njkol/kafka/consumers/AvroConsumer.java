package com.njkol.kafka.consumers;

import java.util.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.njkol.kafka.models.User;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class AvroConsumer {

	public static void main(String[] args) throws Exception {

		String topicName = "user-avro";
		String groupName = "user-avro-group";
		
		String bootstrap_server = "localhost:29092";
	
		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrap_server);
		props.put("group.id", groupName);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "com.njkol.kafka.serde.KafkaAvroDeserializer");
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, User> consumer = null;

		try {
			System.out.println("Started consuming messages ...");
			consumer = new KafkaConsumer<String, User>(props);
			consumer.subscribe(Arrays.asList(topicName));

			while (true) {
				ConsumerRecords<String, User> records = consumer.poll(100);
				for (ConsumerRecord<String, User> record : records) {
					System.out.println("Key : " + record.key());
					System.out.println("Value : " + record.value());
				}
				consumer.commitAsync();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			consumer.commitSync();
			consumer.close();
		}
	}
}
