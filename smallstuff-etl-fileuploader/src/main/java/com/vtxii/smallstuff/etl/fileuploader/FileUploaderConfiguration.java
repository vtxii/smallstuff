package com.vtxii.smallstuff.etl.fileuploader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vtxii.smallstuff.etl.common.Processor;

@Configuration
public class FileUploaderConfiguration {
	@Bean
	public Processor fileProcessor() {
		return new DefaultProcessor();
	}
}