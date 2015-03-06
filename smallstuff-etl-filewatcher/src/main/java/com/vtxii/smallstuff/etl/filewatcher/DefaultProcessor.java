package com.vtxii.smallstuff.etl.filewatcher;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vtxii.smallstuff.etl.common.Processor;

public class DefaultProcessor implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

	@Override
	public void process(Path path) throws Exception {
		logger.info("Got a file to process: {}", path);
		
	}
}
