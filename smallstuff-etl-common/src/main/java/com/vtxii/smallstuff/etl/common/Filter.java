package com.vtxii.smallstuff.etl.common;

import java.nio.file.Path;

/**
 * Filter
 * 
 * Interface for classes that will filter incoming files. 
 */
public interface Filter {

	/**
	 * Takes an incoming file, expressed by the absolute path, and determines if
	 * it should be processed, returning null if it is not, or returning a path to
	 * another file, e.g., get a trigger file and returns the path of the associated 
	 * file.
	 * 
	 * @param path absolute path of the file to be filtered
	 * @throws Exception
	 */
	public Path process(Path path) throws Exception;
}
