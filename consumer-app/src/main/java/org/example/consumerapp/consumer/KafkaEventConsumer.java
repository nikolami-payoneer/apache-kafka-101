/*
 * Copyright (c) 2023 Payoneer Germany GmbH. All rights reserved.
 */
package org.example.consumerapp.consumer;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.Consumer;
import org.example.consumerapp.consumer.handler.ConsumerRecordsHandler;
import org.example.proto.OrderProto;

/**
 * @author nikolami
 */
public class KafkaEventConsumer {

	private final Consumer<String, OrderProto.Order> consumer;
	private final ConsumerRecordsHandler<String, OrderProto.Order> recordsHandler;
	private final String inputTopic;
	private boolean keepConsuming = true;

	public KafkaEventConsumer(final Consumer<String, OrderProto.Order> consumer, final ConsumerRecordsHandler<String, OrderProto.Order> recordsHandler, final String topic) {
		this.consumer = consumer;
		this.recordsHandler = recordsHandler;
		this.inputTopic = topic;
	}

	public void runConsume() {
		try {
			consumer.subscribe(Collections.singletonList(this.inputTopic));
			while (keepConsuming) {
				final var consumerRecords = consumer.poll(Duration.ofSeconds(1));
				recordsHandler.process(consumerRecords);
			}
		} finally {
			consumer.close();
		}
	}

	public void shutdown() {
		keepConsuming = false;
	}
}
