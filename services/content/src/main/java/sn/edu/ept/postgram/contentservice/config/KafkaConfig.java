package sn.edu.ept.postgram.contentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import sn.edu.ept.postgram.shared.events.KafkaTopics;

@Configuration
public class KafkaConfig {

    @Bean
    public EventPublisher eventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new EventPublisher(kafkaTemplate);
    }

    public static class EventPublisher {
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
            this.kafkaTemplate = kafkaTemplate;
        }

        public void publish(String topic, String key, Object event) {
            kafkaTemplate.send(topic, key, event);
        }
    }
}
