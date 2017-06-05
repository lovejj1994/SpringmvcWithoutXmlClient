package spring.springmvc01.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;

import spring.springmvc01.bean.RedisPan;

public class AlertServiceImpl implements AlertService {
	
	private static final Log logger = LogFactory.getLog(AlertServiceImpl.class);
	
	@Autowired
	@Qualifier("jmsTemplate")
	private JmsOperations jmsOperations;
	
	@Override
	public void sendRedisPanAlert(final RedisPan redisPan) {
		jmsOperations.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createObjectMessage(redisPan);
			}
		});
	}

	@Override
	public RedisPan receiveRedisPanAlert() {
		ObjectMessage objectMessage = (ObjectMessage)jmsOperations.receive();
		try {
			RedisPan redisPan = (RedisPan) objectMessage.getObject();
			logger.info("一个redispan被接受，redis的name是"+redisPan.getName());
			return redisPan;
		} catch (JMSException e) {
			throw JmsUtils.convertJmsAccessException(e);
		}
	}

}
