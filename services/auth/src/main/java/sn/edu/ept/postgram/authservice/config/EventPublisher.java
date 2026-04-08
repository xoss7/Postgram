package sn.edu.ept.postgram.authservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import sn.edu.ept.postgram.shared.events.UserRegisteredEvent;

@Slf4j
@AllArgsConstructor
@Configuration
public class EventPublisher {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public void publish(String topic, String key, UserRegisteredEvent event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.info("Published event to topic {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}