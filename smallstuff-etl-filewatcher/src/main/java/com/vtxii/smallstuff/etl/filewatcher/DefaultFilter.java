package com.vtxii.smallstuff.etl.filewatcher;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vtxii.smallstuff.etl.common.Filter;

public class DefaultFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultFilter.class);
	
	@Override
	public Path process(Path path) throws Exception {
		logger.info("Got a file to filter: {}", path);
		return path;
	}
}
