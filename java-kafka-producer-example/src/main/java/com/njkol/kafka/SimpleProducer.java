package com.njkol.kafka;

import java.util.*;
import org.apache.kafka.clients.producer.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleProducer {

	private static final String server= "localhost:29092";
	
	private static final List<String> sellers = Arrays.asList("LNK", "OMA", "KC", "DEN");
	private static final List<Product> products = Arrays.asList(new Product("Toothpaste", 4.99),
			new Product("Toothbrush", 3.99), new Product("Dental Floss", 1.99));

	
	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
		props.put("bootstrap.servers",  server);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	
		demoJsonRecords(props);
	}
	
	private static void demoJsonRecords(Properties props) throws JsonProcessingException, InterruptedException{
		
		String topicName = "sales";
		ObjectMapper mapper = new ObjectMapper();
		int noOfMessagestoProduce = 5;
		long messageProdutionInterval = 5000;
		
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		
		try {
			while(true) {
				for (int i = 0; i <= noOfMessagestoProduce ; i++) {
					String jsonStr = mapper.writeValueAsString(makeSaleitem());
					System.out.println(jsonStr);
					ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, null, jsonStr);
					producer.send(record);
					
				}
				Thread.sleep(messageProdutionInterval);
			}
		}finally {
			producer.close();
		}
	}

	private static Sale makeSaleitem() {
		Random rand = new Random();
		String seller = sellers.get(rand.nextInt(sellers.size()));
		int qty = rand.nextInt(1, 5);
		Product product = products.get(rand.nextInt(products.size()));
		Sale sale = new Sale(seller, product.getProduct(), qty, product.getProduct_price(), System.currentTimeMillis());
		return sale;
	}
	
	private static void demoSimple(Properties props) {
	
		String topicName = "SimpleProducerTopic";
		String key = "Key2";
		String value = "Value-2";
		
		
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, key, value);
		producer.send(record);
		producer.close();

		System.out.println("SimpleProducer Completed.");
	}
}