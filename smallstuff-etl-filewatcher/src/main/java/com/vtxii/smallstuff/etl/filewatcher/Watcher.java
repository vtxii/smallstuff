package com.vtxii.smallstuff.etl.filewatcher;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vtxii.smallstuff.etl.common.Filter;
import com.vtxii.smallstuff.etl.common.LandingManager;
import com.vtxii.smallstuff.etl.common.Processor;

/**
 * Watcher
 * 
 * Runnable that waits on file creations and modifications for a specified directory.
 * The watching process is performed via the NIO WatchService.  Once an event is
 * detected the resulting file processing is then performed by a LandingManager.
 */
class Watcher implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Watcher.class);
	
	private String directory;
	private Path path;
	private int pollingInterval;
	private Class<?> processorClass;
	private Class<?> filterClass;
	private volatile boolean running = true; 
	
	public Watcher(String directory, int pollingInterval, Class<?> processorClass,
			Class<?> filterClass ) throws FileNotFoundException {
		logger.debug("args: {}, {}, {}, {}", directory, pollingInterval, 
				processorClass, filterClass);
		this.pollingInterval = pollingInterval;
		this.processorClass = processorClass;
		this.filterClass = filterClass;
		this.directory = directory;
		
		// Make sure the directory exists
		path = Paths.get(directory, "landing");
		if (false == path.toFile().exists()) {
			throw new FileNotFoundException();
		}
	}

    @Override
    public void run() {
    	WatchKey key = null;
		LandingManager manager = new LandingManager();
    	try {
	    	// Register the directory with the watch service for create
	    	WatchService watcher = path.getFileSystem().newWatchService();
	        path.register(watcher, 
	        		StandardWatchEventKinds.ENTRY_CREATE, 
	        		StandardWatchEventKinds.ENTRY_MODIFY);
	        
	        // Instantiate the processor and filter
	        Processor processor = (null != processorClass) ?
	        		(Processor)processorClass.newInstance() :
	        		null;
	        Filter filter = (null != filterClass) ?
	    	        		(Filter)filterClass.newInstance() :
	    	        		null;
    		logger.debug("processor instance: {}", processor);
    		logger.debug("filter instance: {}", filter);
	        
	        // Loop waiting for a file to be created, polling at the specified
	        // polling interval
	        Kind<?> kind;
	        while(running) {
	            try {
	            	key = watcher.poll(this.pollingInterval, TimeUnit.SECONDS);
	            	if (null == key) {
	            		continue;
	            	}
	            	
	            	// See what kind of event this is
					kind = null;
					List<WatchEvent<?>> events = key.pollEvents();
					for(WatchEvent<?> event : events) {
						kind = event.kind();
						if (StandardWatchEventKinds.OVERFLOW == kind) {
							continue;
							
						// We process both CREATE and MODIFY, but since the files are
						// always moved out of the landing directory, they work the same.
						// This permits stuck files to be 'touched' to get them moving.
						} else if (StandardWatchEventKinds.ENTRY_CREATE == kind ||
								StandardWatchEventKinds.ENTRY_MODIFY == kind) {
							Path newPath = ((WatchEvent<Path>) event).context();
							newPath = Paths.get(path.toString(), newPath.toString());
							if (true == newPath.toFile().exists()) {
								manager.process(newPath.toFile(), processor, filter);
							}
						}
					}
					if(!key.reset()) {
						break;
					}
	    		} catch (Exception e) {
	    			// If there is an exception we try to keep looping, remembering
	    			// to cleanup the lock and the key.
	    			logger.error("exception in watcher loop for directory \"" + 
	    					directory + "\"", e);
					if(!key.reset()) {
						break;
					}	    			
				}
	        }
        } catch (Exception e) {
			logger.error("exception registering directory \"" + directory + "\"", e); 	
        }
    }
    
	void setRunning(boolean running) {
    	this.running = running;
    }
}
