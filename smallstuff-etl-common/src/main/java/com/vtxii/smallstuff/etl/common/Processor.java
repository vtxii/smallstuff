package com.vtxii.smallstuff.etl.common;

import java.nio.file.Path;

/**
 * Processor
 * 
 * Wrapper the application specific processing.
 *
 */
public interface Processor {

	/**
	 * Invokes application specific processing, e.g. staging, DQ, etc.
	 * 
	 * @param path absolute path of the file to be processed
	 * @throws Exception
	 */
	public void process(Path path) throws Exception;
}
