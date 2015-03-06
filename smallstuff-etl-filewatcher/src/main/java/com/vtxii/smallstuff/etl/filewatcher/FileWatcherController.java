package com.vtxii.smallstuff.etl.filewatcher;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileWatcherController {

	private static final Logger logger = LoggerFactory.getLogger(FileWatcherController.class);

	@Autowired
	ServletContext servletContext;
	
	@RequestMapping(value="/status", method=RequestMethod.GET)
	public 	@ResponseBody String status() {
		/**
		Monitor monitor = (Monitor)servletContext.getAttribute("monitor");
		return monitor.getStatus();
		*/
		return "working";
	}
}
