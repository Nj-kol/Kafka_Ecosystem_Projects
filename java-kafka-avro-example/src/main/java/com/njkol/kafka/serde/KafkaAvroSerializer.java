package com.njkol.kafka.serde;

import org.apache.kafka.common.serialization.Serializer;

import com.njkol.kafka.utils.AvroUtils;

import org.apache.kafka.common.errors.SerializationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.nio.ByteBuffer;

public class KafkaAvroSerializer implements Serializer<Object> {

	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	public byte[] serialize(String topic, Object data) {

		byte[] ser = null;

		try {
			ser= AvroUtils.serialize(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		return ser;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
