package com.vtxii.smallstuff.etl.filewatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitor
 * 
 * Provides for starting, stopping, and indicating the status of watchers
 */
public class Monitor {

	private static final Logger logger = LoggerFactory.getLogger(Monitor.class);
	
	private static final int WATCHER_IDX = 0;
	private static final int THREAD_IDX = 1;
	private static final String DELIMITER = ";";
	private Map<String, Object[]> status = new HashMap<String, Object[]>();
	
	/**
	 * Starts the watchers specified in the semicolon delimited directories string.  
	 * A map is created that associates each directory being watched and the resulting 
	 * Watcher object and associated Thread. 
	 * 
	 * @param directories semicolon delimited string of directories 
	 * @param pollingIntervalString string representation of watcher polling interval
	 * @param processorClassName name of the class for processing a file
	 * @param filterClassName name of the class for filtering files
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void start(String directories, String pollingIntervalString, 
			String processorClassName, String filterClassName) throws ClassNotFoundException, IOException {
		logger.debug("args {}, {}, {}, {}", directories, pollingIntervalString, processorClassName, filterClassName);
		// Parse the pollingInterval and directories 
		int pollingInterval = Integer.parseInt(pollingIntervalString);
		String[] array = directories.split(DELIMITER);
		List<String> list = Arrays.asList(array);
		
		// Get the classes for the processor and the filter
		Class<?> processorClass = null;
		if (null != processorClassName && 0 < processorClassName.length()) {
			processorClass = Class.forName(processorClassName);
		}
		logger.debug("processorCLassName length: {}", processorClassName.length());
		Class<?> filterClass = null;
		if (null != filterClassName && 0 < filterClassName.length()) {
			filterClass = Class.forName(filterClassName);
		}
		logger.debug("filterClassName length: {}", filterClassName.length());
		
		// Spawn a thread for each directory
		Thread thread;
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String directory = iterator.next();
			logger.debug("instantiating watcher", directory, pollingInterval);
			Watcher watcher = new Watcher(directory, pollingInterval, processorClass,
					filterClass);
			thread = new Thread(watcher);
			thread.start();
			Object[] objects = new Object[]{watcher, thread};
			status.put(directory, objects);
		}
	};

	/**
	 * Stops Watchers by iterating over the map of directories, Watchers, and Threads.
	 */
	public void stop() {
		String directory;
		Object[] objects;
		Watcher watcher;
		Thread thread;
		Entry<String, Object[]> entry;
		Iterator<Entry<String, Object[]>> iterator = this.status.entrySet().iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			directory = entry.getKey();
			objects = entry.getValue();
			watcher = (Watcher) objects[WATCHER_IDX];
			thread = (Thread) objects[THREAD_IDX];
			logger.debug("stopping watcher", directory, watcher, thread);
			if (true == thread.isAlive()) {
				watcher.stop();
				try {
					thread.join();
				} catch (InterruptedException e) {
					logger.error("unable to join thread as part of stopping the thread", e);
				}
			}
		}
	};
		
	/**
	 * Generate JSON indicating the watched directory and thread status by iterating
	 * over the map of directories, Watchers, and Threads.
	 * 
	 * @return JSON vector of objects with directory and Thread status
	 */
	public String getStatus() {
		String json = "[";
		String status;
		String comma;
		String directory;
		Thread thread;
		Object[] objects;
		Entry<String, Object[]> entry;
		Iterator<Entry<String, Object[]>> iterator = this.status.entrySet().iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			directory = entry.getKey();
			objects = entry.getValue();
			thread = (Thread)objects[THREAD_IDX];
			status = (thread.isAlive()) ? "running" : "stopped";
			comma = (iterator.hasNext()) ? "," : "";
			json += "{directory: \"" + directory + "\", status: \"" + status + "\"}" + comma;
		}
		json += "]";
				
		return json;
	}
}
