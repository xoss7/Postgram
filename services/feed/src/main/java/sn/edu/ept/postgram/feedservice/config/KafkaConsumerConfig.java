package sn.edu.ept.postgram.feedservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import sn.edu.ept.postgram.feedservice.dto.event.PostDeletedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostInteractionEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostPublishedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserFollowedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserUnfollowedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostPublishedEvent> postPublishedKafkaListenerContainerFactory() {
        return listenerFactory(PostPublishedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostInteractionEvent> postInteractionKafkaListenerContainerFactory() {
        return listenerFactory(PostInteractionEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserFollowedEvent> userFollowedKafkaListenerContainerFactory() {
        return listenerFactory(UserFollowedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserUnfollowedEvent> userUnfollowedKafkaListenerContainerFactory() {
        return listenerFactory(UserUnfollowedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostDeletedEvent> postDeletedKafkaListenerContainerFactory() {
        return listenerFactory(PostDeletedEvent.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> listenerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("sn.edu.ept.postgram.feedservice.dto.event");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
}
