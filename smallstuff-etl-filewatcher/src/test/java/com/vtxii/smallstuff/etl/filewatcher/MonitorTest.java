package com.vtxii.smallstuff.etl.filewatcher;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.junit.Test;

public class MonitorTest {
	
	// Parameters for getting thread information
	private static final int MAX_COUNT = 5;
	private static final long MAX_SLEEP = 100; 
	
	// Test directories
	private static final String DIRECTORIES = "/home/devuser/test/;/home/devuser/test2/;";
	private static final String BAD_DIRECTORIES = "/home/devuser/foo;/home/devuser/test2/;";
	
	@Test
	public void testStartStop() {
		Monitor monitor = new Monitor();
		try {
			monitor.start(DIRECTORIES, "1", null, null);
			assertTrue(hasThreadNames(DIRECTORIES));
			monitor.stop();
			assertTrue(!hasThreadNames(DIRECTORIES));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}

	@Test
	public void testStartStopWithOneBadDirectory() {
		Monitor monitor = new Monitor();
		try {
			monitor.start(BAD_DIRECTORIES, "1", null, null);
			String status = monitor.getStatus();
			assert(-1 == status.indexOf("foo"));
			assert(-1 < status.indexOf("test2"));
			monitor.stop();
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testStartStopRestart() {
		Monitor monitor = new Monitor();
		try {
			monitor.start(DIRECTORIES, "1", null, null);
			assertTrue(hasThreadNames(DIRECTORIES));
			monitor.stop();
			assertTrue(!hasThreadNames(DIRECTORIES));
			monitor.restart();
			assertTrue(hasThreadNames(DIRECTORIES));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testStopStop() {
		Monitor monitor = new Monitor();
		try {
			monitor.stop();
			assertTrue(!hasThreadNames(DIRECTORIES));
			monitor.stop();
			assertTrue(!hasThreadNames(DIRECTORIES));
		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}
	
	@Test
	public void testRestartRestart() {
		Monitor monitor = new Monitor();
		try {
			monitor.restart();
			assertTrue(!hasThreadNames(DIRECTORIES));
			monitor.restart();
			assertTrue(!hasThreadNames(DIRECTORIES));
		} catch (Exception e) {
			fail("with exception: " + e);
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
}
