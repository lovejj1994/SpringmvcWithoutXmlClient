package spring.springmvc01.service;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import spring.springmvc01.bean.RedisPan;

@Component
public class RedisPanAlertHandler implements MessageDelegate {

	private static final Log logger = LogFactory.getLog(RedisPanAlertHandler.class);

	public void handleRedisPanAlert(RedisPan redisPan) {
		logger.info("jms--一个redispan被接受，redis的name是" + redisPan.getName());
	}
	
	public void rabbithandleRedisPanAlert(RedisPan redisPan) {
		logger.info("rabbit--一个redispan被接受，redis的name是" + redisPan.getName());
	}

	@Override
	public void handleMessage(String message) {
		logger.info("一个请求被接受:" + message);
	}

	@Override
	public void handleMessage(Map message) {
		logger.info("一个请求被接受:" + message.toString());
	}

	@Override
	public void handleMessage(byte[] message) {
		logger.info("一个请求被接受:" + message.toString());
	}

	@Override
	public void handleMessage(Serializable message) {
		logger.info("一个请求被接受:" + message.toString());
	}
}
