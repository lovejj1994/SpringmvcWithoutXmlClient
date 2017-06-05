package spring.springmvc01.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Service
public class SomeHandler implements ErrorHandler {

	private static final Log logger = LogFactory.getLog(SomeHandler.class);
	
	@Override
	public void handleError(Throwable t) {
		logger.error("Error in listener", t);

	}

}
