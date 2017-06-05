package spring.springmvc01.config;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.type.TypeFactory;

import spring.springmvc01.bean.RedisPan;
import spring.springmvc01.service.HttpRedisService;
import spring.springmvc01.service.RedisPanAlertHandler;
import spring.springmvc01.service.RmiRedisService;
import spring.springmvc01.service.SomeHandler;

@Configuration
@ComponentScan(basePackages = { "spring.springmvc01" }, excludeFilters = {
		// 不扫描@EnableWebMvc注解的组件
		@Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class) }, scopedProxy = ScopedProxyMode.TARGET_CLASS)
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringConfig {

	// rmi 客户端
	@Bean
	public RmiProxyFactoryBean rmiProxyFactoryBean() {
		RmiProxyFactoryBean rmiBean = new RmiProxyFactoryBean();
		rmiBean.setServiceInterface(RmiRedisService.class);
		rmiBean.setServiceUrl("rmi://192.168.86.1/RmiRedisService");
		return rmiBean;
	}

	// http 客户端
	@Bean
	public HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean() {
		HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
		proxy.setServiceInterface(HttpRedisService.class);
		proxy.setServiceUrl("http://localhost:8080/SpringmvcWithoutXml/redisservice");
		return proxy;
	}

	// spring-activemq

	// activemq连接工厂
	@Bean
	public ActiveMQConnectionFactory mqConnectionFactory() {
		ActiveMQConnectionFactory mqConnectionFactory = new ActiveMQConnectionFactory();
		mqConnectionFactory.setBrokerURL("tcp://pan:61616");
		return mqConnectionFactory;
	}

	@Bean
	public MessageConverter messageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTypeIdPropertyName(TypeFactory.defaultInstance().constructType(RedisPan.class).toString());
		return converter;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(mqConnectionFactory());
		jmsTemplate.setDefaultDestinationName("test.queue");
		jmsTemplate.setMessageConverter(messageConverter());
		return jmsTemplate;
	}

	// 监听器（适配器）
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisPanAlertHandler handler) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(handler);
		adapter.setDefaultListenerMethod("handleRedisPanAlert");
		adapter.setMessageConverter(messageConverter());
		return adapter;
	}

	// 监听容器
	@Bean
	public DefaultMessageListenerContainer listenerContainer(MessageListenerAdapter messageListenerAdapte,
			SomeHandler someHandler) {
		DefaultMessageListenerContainer jmsListenerContainerFactory = new DefaultMessageListenerContainer();
		jmsListenerContainerFactory.setConnectionFactory(mqConnectionFactory());
		jmsListenerContainerFactory.setDestinationName("test.queue");
		jmsListenerContainerFactory.setMessageListener(messageListenerAdapte);
		jmsListenerContainerFactory.setMessageConverter(messageConverter());
		jmsListenerContainerFactory.setErrorHandler(someHandler);
		return jmsListenerContainerFactory;
	}

	// spring-amqp
	
	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost", 5672);
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		return connectionFactory;
	}

	@Bean
	public org.springframework.amqp.support.converter.MessageConverter rabbitMessageConverter() {
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		return converter;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory());
	}
	
	@Bean
	public Queue rabbitQueue(){
		return new Queue("test.rabbit.queue");
	}
	
	// 监听器（适配器）
	@Bean(name="rabbitMessageListenerAdapter")
	public org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter rabbitMessageListenerAdapter(RedisPanAlertHandler handler) {
		org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter adapter = new org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter(handler);
		adapter.setDefaultListenerMethod("rabbithandleRedisPanAlert");
		adapter.setMessageConverter(rabbitMessageConverter());
		return adapter;
	}

	// 监听容器
	@Bean
	public SimpleMessageListenerContainer rabbitlistenerContainer(org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter rabbitMessageListenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setMessageListener(rabbitMessageListenerAdapter);
		container.setMessageConverter(rabbitMessageConverter());
		container.setQueueNames("test.rabbit.queue");
		return container;  
	}
	
	
}
