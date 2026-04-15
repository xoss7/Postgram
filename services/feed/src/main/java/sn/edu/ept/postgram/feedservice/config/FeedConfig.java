package sn.edu.ept.postgram.feedservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeedProperties.class)
public class FeedConfig {
}
