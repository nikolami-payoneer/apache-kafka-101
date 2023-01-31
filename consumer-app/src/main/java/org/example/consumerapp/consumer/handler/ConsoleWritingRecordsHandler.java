/*
 * Copyright (c) 2023 Payoneer Germany GmbH. All rights reserved.
 */
package org.example.consumerapp.consumer.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.example.proto.OrderProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nikolami
 */
public class ConsoleWritingRecordsHandler implements ConsumerRecordsHandler<String, OrderProto.Order> {

	private static final Logger LOG = LoggerFactory.getLogger(ConsoleWritingRecordsHandler.class);

	@Override
	public void process(final ConsumerRecords<String, OrderProto.Order> consumerRecords) {
		final var valueList = new ArrayList<OrderProto.Order>();
		consumerRecords.forEach(record -> valueList.add(record.value()));
		if (!valueList.isEmpty()) {
			valueList.forEach(it -> LOG.info(it.toString()));
		}
	}
}
