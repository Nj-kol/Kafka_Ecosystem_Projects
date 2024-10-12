package com.njkol.kafka.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.njkol.kafka.utils.AvroUtils;

public class KafkaAvroDeserializer implements Deserializer<Object> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object deserialize(String topic, byte[] data) {

		Object deser = null;

		try {
			deser= AvroUtils.deSerialize(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		return deser;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
