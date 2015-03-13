package com.vtxii.smallstuff.etl.filewatcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorTest {

	private static final Logger logger = LoggerFactory.getLogger(MonitorTest.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// Parameters for getting thread information
	private static final int MAX_COUNT = 5;
	private static final long MAX_SLEEP = 100; 
	
	@Test
	public void testStartStop() {
		Monitor monitor = new Monitor();
		try {
			String directories = genDirectories(new String[]{"test1", "test2"});
			monitor.start(directories, "1", null, null);
			assertTrue(hasThreadNames(directories));
			monitor.stop();
			assertTrue(!hasThreadNames(directories));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}

	@Test
	public void testStartStopWithOneBadDirectory() {
		Monitor monitor = new Monitor();
		try {
			String directories = genDirectories(new String[]{"test1"});
			directories += ";foo";
			monitor.start(directories, "1", null, null);
			String status = monitor.getStatus();
			logger.info("status: {}", status);
			assert(-1 == status.indexOf("foo"));
			assert(-1 < status.indexOf("test1"));
			monitor.stop();
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testStartStopRestart() {
		Monitor monitor = new Monitor();
		try {
			String directories = genDirectories(new String[]{"test1", "test2"});
			monitor.start(directories, "1", null, null);
			assertTrue(hasThreadNames(directories));
			monitor.stop();
			assertTrue(!hasThreadNames(directories));
			monitor.restart();
			assertTrue(hasThreadNames(directories));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testStopStop() {
		Monitor monitor = new Monitor();
		try {
			String directories = genDirectories(new String[]{"test1", "test2"});
			monitor.stop();
			assertTrue(!hasThreadNames(directories));
			monitor.stop();
			assertTrue(!hasThreadNames(directories));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testRestartRestart() {
		Monitor monitor = new Monitor();
		try {
			String directories = genDirectories(new String[]{"test1", "test2"});
			monitor.restart();
			assertTrue(!hasThreadNames(directories));
			monitor.restart();
			assertTrue(!hasThreadNames(directories));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@After
	public void teardown() {
		try {
			// Give the TemporaryFile a chance to cleanup
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check for the names of the directories in the thread dump.  The
	 * directories are used as the thread name.
	 * 
	 * @param directories semicolon delimited list of directories to watch
	 * @return true if all of the directories are in the thread dump
	 * @throws InterruptedException
	 */
	private boolean hasThreadNames(String directories) throws InterruptedException {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		String[] array = directories.split(";");
		String threadName;
		for (int count=0; count<MAX_COUNT; count++) {
			ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds());
			boolean found = false;
			for (String directory : array) {
				found = false;
				for (ThreadInfo threadInfo : threadInfos) {
					threadName = threadInfo.getThreadName();
					if (true == directory.equals(threadName)) {
						found = true;
						break;
					}
				}
				if (false == found) {
					break;
				}
			}
			if (true == found) {
				return true;
			}
			Thread.sleep(MAX_SLEEP);
		}
		return false;
	}
	
	/**
	 * Creates a string of semicolon delimited directories from the argument of an array of strings 
	 * and creates the associated directories, with the standard directories, off of the TemporaryFolder
	 * 
	 * @param testDirectories array of directory names
	 * @return string of semicolon delimited directories
	 */
	private String genDirectories(String[] testDirectories) {
		String directories = "";
		try {
			File testFolder = null;
			for (int idx=0; idx<testDirectories.length; idx++) {
				testFolder = folder.newFolder(testDirectories[idx]);
				directories += testFolder.getAbsolutePath();
				directories += (idx<testDirectories.length-1) ? ";" : "";
				Paths.get(testFolder.getAbsolutePath(), "landing").toFile().mkdir();
				Paths.get(testFolder.getAbsolutePath(), "working").toFile().mkdir();
				Paths.get(testFolder.getAbsolutePath(), "archive").toFile().mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return directories;
	}
}