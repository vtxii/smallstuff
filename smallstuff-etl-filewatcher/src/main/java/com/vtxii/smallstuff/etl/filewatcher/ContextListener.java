package com.vtxii.smallstuff.etl.filewatcher;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContextListener
 * 
 * Kicks off the threads for watching for changes to a directory (files created/modified)
 * when the servlet is started.
 */
public class ContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);

	static final String DIRECTORIES = "directories";
	static final String PROCESSOR_CLASS_NAME = "processor-class-name";
	static final String FILTER_CLASS_NAME = "filter-class-name";
	static final String MONITOR = "monitor";
	static final String POLLING_INTERVAL = "polling-interval";
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		try {
			// Get the monitor, stop the watchers, and remove the monitor 
			// from the context
			Monitor monitor = (Monitor)context.getAttribute(MONITOR);
			logger.debug("stopping monitor");
			monitor.stop();
			context.removeAttribute(MONITOR);
		} catch (Exception e) {
			logger.error("exception destroying context", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		try {			
			// Get a semicolon delimited set of directories that are going to be watched, 
			// the classes that are going to be run when a file is detected, and the file
			// watching polling interval.
			String processorClassName =  context.getInitParameter(PROCESSOR_CLASS_NAME);
			String filterClassName =  context.getInitParameter(FILTER_CLASS_NAME);
			String directories =  context.getInitParameter(DIRECTORIES);
			String pollingInterval = context.getInitParameter(POLLING_INTERVAL);
			logger.debug("context variable processorClassName {}", processorClassName);
			logger.debug("context variable filterClassName {}", filterClassName);
			logger.debug("context variable directories {}", directories);
			logger.debug("context variable pollingInterval {}", pollingInterval);
			
			// Instantiate a monitor that will kickoff the watchers and save it in the 
			// context so that the controller can access it
			Monitor monitor = new Monitor();
			monitor.start(directories, pollingInterval,processorClassName, filterClassName );
			context.setAttribute(MONITOR, monitor);
			
		} catch (Exception e) {
			logger.error("exception initilaizing context", e);
		}
	}
}
