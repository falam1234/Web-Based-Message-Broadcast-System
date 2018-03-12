package com.message.redis.pubsub.config;
 
import java.util.concurrent.Executors;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
 

import com.message.redis.pubsub.consumer.CustomerInfoSubscriber;
import com.message.redis.pubsub.producer.CustomerInfoPublisher;
import com.message.redis.pubsub.producer.RedisCustomerInfoPublisher;
 
@Configuration
@ComponentScan("com.message.redis.pubsub")
public class RedisConfig {
 
	@Bean
	JedisConnectionFactory getJedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();	
	    jedisConnectionFactory.setHostName("localhost");
	    jedisConnectionFactory.setPort(6379);
		return new JedisConnectionFactory();
	}
 
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		return template;
	}
 
	private RedisConnectionFactory jedisConnectionFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new CustomerInfoSubscriber());
	}
 
	@Bean
	RedisMessageListenerContainer redisContainer() {
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory());
		container.addMessageListener(messageListener(), topic());
		container.setTaskExecutor(Executors.newFixedThreadPool(4));
		return container;
	}
 
	@Bean
	CustomerInfoPublisher redisPublisher() {
		return new RedisCustomerInfoPublisher(redisTemplate(), topic());
	}
 
	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("pubsub:jsa-channel");
	}
}
