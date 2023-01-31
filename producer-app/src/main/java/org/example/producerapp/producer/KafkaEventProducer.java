/*
 * Copyright (c) 2023 Payoneer Germany GmbH. All rights reserved.
 */
package org.example.producerapp.producer;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.example.proto.OrderProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nikolami
 */
public class KafkaEventProducer {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaEventProducer.class);

	private final Producer<String, OrderProto.Order> producer;
	private final String outTopic;

	public KafkaEventProducer(final Producer<String, OrderProto.Order> producer, final String topic) {
		this.producer = producer;
		this.outTopic = topic;
	}

	public Future<RecordMetadata> produce(final String key, final OrderProto.Order value) {
		final var producerRecord = new ProducerRecord<>(outTopic, key, value);

		return producer.send(producerRecord);
	}

	public void printMetadata(final Collection<Future<RecordMetadata>> metadata) {
		metadata.forEach(m -> {
			try {
				final RecordMetadata recordMetadata = m.get();
				LOG.info("Record written to offset " + recordMetadata.offset() + " timestamp " + recordMetadata.timestamp());
			} catch (InterruptedException | ExecutionException e) {
				if (e instanceof InterruptedException) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public void shutdown() {
		producer.close();
	}
}
