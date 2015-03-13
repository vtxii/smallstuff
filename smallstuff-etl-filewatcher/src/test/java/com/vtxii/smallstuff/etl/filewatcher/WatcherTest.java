package com.vtxii.smallstuff.etl.filewatcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatcherTest {

	private static final Logger logger = LoggerFactory.getLogger(WatcherTest.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static final long POLLING_INTERVAL = 10; 
	private static final int RECORD_COUNT = 100;
	private static final long DELAY = 100; 

	
	@Test
	public void testWaitOnLock() {
		try {
			// Create test folder with standard children folders
			File testFolder = folder.newFolder("test");
			File landingFolder = Paths.get(testFolder.getAbsolutePath(), "landing").toFile();
			landingFolder.mkdir();
			File workingFolder = Paths.get(testFolder.getAbsolutePath(), "working").toFile();
			workingFolder.mkdir();
			File archiveFolder = Paths.get(testFolder.getAbsolutePath(), "archive").toFile();
			archiveFolder.mkdir();
		
			// Start a thread with the watcher
			String directories = testFolder.getAbsolutePath();
			logger.info("directories: {}", directories);
			Watcher watcher = new Watcher(directories, POLLING_INTERVAL, null, null);
			Thread thread = new Thread(watcher);
			thread.start();
			
			// Slowly write a file with sleep >= pollingInterval so we
			// know it will have to wait
			slowWriter(Paths.get(landingFolder.getAbsolutePath(), "foo"), RECORD_COUNT, DELAY);
			
			// Give the watcher thread a chance to process the file
			Thread.sleep(1000);
			
			// Check that the landing and working folders are empty and the 
			// archive folder has one file
			assert(0 == landingFolder.listFiles().length);
			assert(0 == workingFolder.listFiles().length);
			assert(1 == archiveFolder.listFiles().length);
			
			// Don't leave stuff behind
			watcher.setRunning(false);
			thread.join();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Slowly writes a file specified by the path, consisting of the number of count records, that are written
	 * with a delay between writes.
	 * 
	 * @param path file specification for the file to be written
	 * @param count number of records to be written
	 * @param delay the delay in milliseconds between writes
	 */
	private void slowWriter(Path path, int count, long delay) {
		String line = "hello world";
	    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
	      for(int idx=0; idx<count; idx++){
	        writer.write(line);
	        Thread.sleep(delay);
	      }
	      writer.close();
	    } catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}		
	}
}


