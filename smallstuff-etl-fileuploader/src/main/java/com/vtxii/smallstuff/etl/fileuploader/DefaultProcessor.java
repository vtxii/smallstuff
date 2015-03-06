package com.vtxii.smallstuff.etl.fileuploader;

import java.nio.file.Path;

import com.vtxii.smallstuff.etl.common.Processor;

public class DefaultProcessor implements Processor {

	@Override
	public void process(Path path) throws Exception {
		System.out.println("Got a file: " + path.toFile().getName());
	}
}
