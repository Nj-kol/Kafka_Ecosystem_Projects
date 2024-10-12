package com.njkol.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleConsumer {

	private static final String server= "localhost:29092";
	
	public static void main(String[] args) throws Exception {
		
		String groupName = "SalesTopicGroup";

		Properties props = new Properties();
		props.put("bootstrap.servers", server);
		props.put("group.id", groupName);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("enable.auto.commit", "false");

		demoJsonRecords(props);
	}
	
	private static void demoJsonRecords(Properties props) throws JsonProcessingException, InterruptedException{
	
		ObjectMapper mapper = new ObjectMapper();
		
		String topicName = "sales";
		
		KafkaConsumer<String, String> consumer = null;
		
		try {
			consumer = new KafkaConsumer<String, String>(props);
			consumer.subscribe(Arrays.asList(topicName));

			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				
				for (ConsumerRecord<String, String> record : records) {
					String jsonRec = record.value();
					System.out.println("Received : " + jsonRec);
					// Deserialize JSON to Java Object
					Sale asale = mapper.readValue(jsonRec, Sale.class);
				    System.out.println("Seller Id : " + asale.getSeller_id());
				    System.out.println("Product name : " + asale.getProduct());
				    System.out.println("Product quantity : " + asale.getQuantity());
				    System.out.println("Product price : " + asale.getProduct_price());
				    System.out.println("Sale timestamp : " + asale.getSale_ts());
	
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
