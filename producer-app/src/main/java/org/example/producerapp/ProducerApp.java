package org.example.producerapp;/*
 * Copyright (c) ${YEAR} Payoneer Germany GmbH. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.example.producerapp.producer.KafkaEventProducer;
import org.example.proto.OrderProto;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;

public class ProducerApp {
	public static void main(String[] args) throws IOException {
		final Properties props = loadProperties();
		final var eventProducer = setUpEventProducer(props);
		Runtime.getRuntime().addShutdownHook(new Thread(eventProducer::shutdown));

		final var orders = List.of(
			OrderProto.Order.newBuilder().setId(1L).setArticle("Article1").setAmount(10).build(),
			OrderProto.Order.newBuilder().setId(2L).setArticle("Article2").setAmount(20).build(),
			OrderProto.Order.newBuilder().setId(3L).setArticle("Article3").setAmount(30).build(),
			OrderProto.Order.newBuilder().setId(4L).setArticle("Article4").setAmount(40).build(),
			OrderProto.Order.newBuilder().setId(5L).setArticle("Article5").setAmount(50).build()
		);
		List<Future<RecordMetadata>> metadata = orders.stream()
			.map(order -> eventProducer.produce(String.valueOf(order.getId()), order))
			.toList();

		eventProducer.printMetadata(metadata);
	}

	private static KafkaEventProducer setUpEventProducer(Properties props) {
		final Map<String, Object> config = new HashMap<>();
		props.forEach((key, value) -> config.put((String) key, value));
		config.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
		final var producer = new KafkaProducer<String, OrderProto.Order>(config);

		final String outTopic = props.getProperty("output.topic.name");

		return new KafkaEventProducer(producer, outTopic);
	}

	private static Properties loadProperties() throws IOException {
		Properties envProps = new Properties();
		try (
			final var configStream = ProducerApp.class.getClassLoader().getResourceAsStream("configuration/dev.properties");
			final var input = new BufferedReader(new InputStreamReader(Objects.requireNonNull(configStream)))) {
			envProps.load(input);
		}

		return envProps;
	}
}