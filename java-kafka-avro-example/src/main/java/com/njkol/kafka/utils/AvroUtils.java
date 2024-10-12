package com.njkol.kafka.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import org.apache.kafka.common.errors.SerializationException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;

/**
 * Avro utility
 * 
 * @author Nilanjan Sarkar
 */
public class AvroUtils {

	private final static EncoderFactory encoderFactory = EncoderFactory.get();

	/**
	 * 
	 * @param typeKey
	 * @param inputData
	 * @return
	 * @throws IOException
	 */
	public static <T extends SpecificRecordBase> byte[] writeToMemory(Class<T> typeKey, T inputData)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DatumWriter<T> outputDatumWriter = new SpecificDatumWriter<T>(typeKey);
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
		outputDatumWriter.write(inputData, encoder);
		encoder.flush();
		return baos.toByteArray();
	}



	public static byte[] serialize(Object object) throws IOException {

		Schema schema = new Schema.Parser().parse(new File("./src/main/resources/user_v1.avsc"));

		// null needs to treated specially since the client most likely
		// just wants to send an individual null value instead of making
		// the subject a null type.

		// Also,null in Kafka has a special meaning for deletion
		// in a topic with the compact retention policy.

		// Therefore, we will bypass schema registration and return
		// a null value in Kafka, instead of an Avro encoded null

		if (object == null) {
			return null;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (object instanceof byte[]) {
				out.write((byte[]) object);
			} else {
				BinaryEncoder encoder = encoderFactory.directBinaryEncoder(out, null);
				DatumWriter<Object> writer;
				Object value = object instanceof NonRecordContainer ? ((NonRecordContainer) object).getValue() : object;
				if (value instanceof SpecificRecord) {
					writer = new SpecificDatumWriter<>(schema);
				} else {
					writer = new GenericDatumWriter<>(schema);
				}
				writer.write(value, encoder);
				encoder.flush();
			}
			byte[] bytes = out.toByteArray();
			out.close();
			return bytes;
		} catch (IOException | RuntimeException e) {
			// avro serialization can throw AvroRuntimeException, NullPointerException,
			// ClassCastException, etc
			throw new RuntimeException("Error serializing Avro message", e);
		}
	}

	/**
	 * 
	 * @param payload
	 * @return
	 */
	public static Object deSerialize(byte[] payload) {

		Object result = null;
		int id = -1;
		try {
			ByteBuffer buffer = ByteBuffer.wrap(payload);
			id = buffer.getInt();
			Schema schema = new Schema.Parser().parse(new File("./src/main/resources/user_v1.avsc"));

			int length = buffer.limit() - 1;
			if (schema.getType().equals(Schema.Type.BYTES)) {
				byte[] bytes = new byte[length];
				buffer.get(bytes, 0, length);
				result = bytes;
			} else {
				int start = buffer.position() + buffer.arrayOffset();
				DatumReader reader = new SpecificDatumReader(schema);
				DecoderFactory decoder = DecoderFactory.get();
				BinaryDecoder bd = decoder.binaryDecoder(payload, null);
				Object object = reader.read(null, bd);
				if (schema.getType().equals(Schema.Type.STRING)) {
					object = object.toString(); // Utf8 -> String
				}
				result = object;
			}
		} catch (Exception e) {

		}
		return result;
	}
}
