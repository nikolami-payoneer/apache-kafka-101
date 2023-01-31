/*
 * Copyright (c) 2023 Payoneer Germany GmbH. All rights reserved.
 */
package org.example.consumerapp.consumer.handler;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @author nikolami
 */
public interface ConsumerRecordsHandler<K, V> {
	void process(ConsumerRecords<K, V> consumerRecords);
}
