package org.example.consumerapp;/*
 * Copyright (c) ${YEAR} Payoneer Germany GmbH. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.example.consumerapp.consumer.KafkaEventConsumer;
import org.example.consumerapp.consumer.handler.ConsoleWritingRecordsHandler;
import org.example.proto.OrderProto;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;

public class ConsumerApp {
	public static void main(String[] args) throws IOException {
		final var props = loadProperties();
		final var eventConsumer = setUpEventConsumer(props);
		Runtime.getRuntime().addShutdownHook(new Thread(eventConsumer::shutdown));
		eventConsumer.runConsume();
	}

	private static KafkaEventConsumer setUpEventConsumer(Properties props) {
		props.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, OrderProto.Order.class);

		final var consumer = new KafkaConsumer<String, OrderProto.Order>(props);
		final var inputTopic = props.getProperty("input.topic.name");
		final var handler = new ConsoleWritingRecordsHandler();

		return new KafkaEventConsumer(consumer, handler, inputTopic);
	}

	private static Properties loadProperties() throws IOException {
		Properties envProps = new Properties();
		try (
			final var configStream = ConsumerApp.class.getClassLoader().getResourceAsStream("configuration/dev.properties");
			final var input = new BufferedReader(new InputStreamReader(Objects.requireNonNull(configStream)))) {
			envProps.load(input);
		}

		return envProps;
	}
}
