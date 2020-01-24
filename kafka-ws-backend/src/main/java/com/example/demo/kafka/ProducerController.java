package com.example.demo.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Constants;
import com.example.demo.model.Model;

/**
 * Generate sample messages.
 *
 * Use: GET /kafla/sample/{amount}.
 */
@RestController
@RequestMapping(value = "/api/kafka")
public class ProducerController {

	private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

	private ProducerCallback producerCallback = new ProducerCallback();

	@Autowired
	private KafkaTemplate<String, Model> kafkaTemplate;

	@RequestMapping(value = "/sample/{amount}", method = RequestMethod.GET)
	public void generateMessages(@PathVariable("amount") Integer amount) {

		IntStream.range(0, amount)
			.peek(i -> this.waitFor(1))
			.mapToObj(i -> new Model("Message " + i))
			.forEach(this::sendToKafka);
	}

	private void sendToKafka(Model model) {
		this.kafkaTemplate
			.send(Constants.KAFKA_TOPIC, UUID.randomUUID().toString(), model)
			.addCallback(this.producerCallback);
	}

	private void waitFor(int seconds) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Future<Void> future = scheduler.schedule(() -> null, seconds, TimeUnit.SECONDS);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	class ProducerCallback implements ListenableFutureCallback<SendResult<String, Model>> {
		@Override
		public void onSuccess(SendResult<String, Model> result) {
			RecordMetadata record = result.getRecordMetadata();
			LOG.info("Sending {} to topic {} - partition {}",
					result.getProducerRecord().key(),
					result.getProducerRecord().topic(),
					record.partition());
		}

		@Override
		public void onFailure(Throwable ex) {
			LOG.error("Producer Failure", ex);
		}
	}

}
