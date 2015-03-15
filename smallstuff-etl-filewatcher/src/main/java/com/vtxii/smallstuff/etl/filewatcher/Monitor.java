/**
* Copyright 2015 VTXii
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.vtxii.smallstuff.etl.filewatcher;

import java.io.FileNotFoundException;
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
 * 
 * TODO:  move from Thread to Executor
 */
class Monitor {

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
	void start(String directories, String pollingIntervalString, 
			String processorClassName, String filterClassName) throws ClassNotFoundException, IOException {
		logger.debug("args {}, {}, {}, {}", directories, pollingIntervalString, processorClassName, filterClassName);
		// Parse the pollingInterval and directories 
		int pollingInterval = Integer.parseInt(pollingIntervalString);
		String[] array = directories.split(DELIMITER);
		List<String> list = Arrays.asList(array);
		
		// Get the classes for the processor and the filter
		Class<?> processorClass = null;
		if (null != processorClassName && 0 < processorClassName.length()) {
			logger.debug("processorCLassName length: {}", processorClassName.length());
			processorClass = Class.forName(processorClassName);
		}
		Class<?> filterClass = null;
		if (null != filterClassName && 0 < filterClassName.length()) {
			logger.debug("filterClassName length: {}", filterClassName.length());
			filterClass = Class.forName(filterClassName);
		}
		
		// Spawn a thread for each directory
		Thread thread;
		for (String entry : list) {
			String directory = entry;
			try {
				Watcher watcher = new Watcher(directory, pollingInterval, processorClass,
						filterClass);
				thread = new Thread(watcher);
				thread.setName(directory);
				thread.start();
				Object[] objects = new Object[]{watcher, thread};
				status.put(directory, objects);
			} catch (FileNotFoundException e) {
				logger.error("directory doesn't exists exception: {}", directory);
			}
		}
	};

	/**
	 * Stops Watchers by iterating over the map of directories, Watchers and Threads and
	 * invoking the Watcher stop method.
	 */
	void stop() {
		String directory;
		Object[] objects;
		Watcher watcher;
		Thread thread;
		for (Entry<String, Object[]> entry :  status.entrySet()) {
			directory = entry.getKey();
			objects = entry.getValue();
			watcher = (Watcher) objects[WATCHER_IDX];
			thread = (Thread) objects[THREAD_IDX];
			if (true == thread.isAlive()) {
				logger.debug("stopping watcher: {}, {}, {}", directory, watcher, thread);
				watcher.setRunning(false);
				try {
					thread.join();
				} catch (InterruptedException e) {
					logger.error("stop exception: {}", e);
				}
			}
		}
	};

	/**
	 * Restart Watchers by iterating over the map of directories, Watchers and Threads; checking
	 * if the thread is running; and starting a new thread for threads that have stopped.
	 */
	void restart() {
		String directory;
		Object[] objects;
		Watcher watcher;
		Thread thread;
		for (Entry<String, Object[]> entry :  status.entrySet()) {
			directory = entry.getKey();
			objects = entry.getValue();
			watcher = (Watcher) objects[WATCHER_IDX];
			thread = (Thread) objects[THREAD_IDX];
			if (true != thread.isAlive()) {
				logger.debug("restarting watcher: {}, {}, {}", directory, watcher, thread);
				watcher.setRunning(true);
				thread = new Thread(watcher);
				thread.setName(directory);
				thread.start();
				objects[THREAD_IDX] = thread;
			}
		}
	};
	
	/**
	 * Generate JSON indicating the watched directory and thread status by iterating
	 * over the map of directories, Watchers and Threads.
	 * 
	 * @return JSON vector of objects with directory and Thread status
	 */
	String getStatus() {
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
