/**
* Copyright 2015 VTXii
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.vtxii.smallstuff.etl.filewatcher;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileWatcherController {

	private static final Logger logger = LoggerFactory.getLogger(FileWatcherController.class);

	@RequestMapping(value="/status", method=RequestMethod.GET)
	public 	@ResponseBody String handleStatusRequest(HttpServletRequest request) {
		Monitor monitor = (Monitor)request.
				getSession().
				getServletContext().
				getAttribute(ContextListener.MONITOR);
		logger.debug("handling status request with monitor: {}", monitor);
		return monitor.getStatus();
	}

	@RequestMapping(value="/restart", method=RequestMethod.GET)
	public 	@ResponseBody String handleRestartRequest(HttpServletRequest request) {
		Monitor monitor = (Monitor)request.
				getSession().
				getServletContext().
				getAttribute(ContextListener.MONITOR);
		logger.debug("handling restart request with monitor: {}", monitor);
		monitor.restart();
		return monitor.getStatus();
	}

	@RequestMapping(value="/stop", method=RequestMethod.GET)
	public 	@ResponseBody String handleStopRequest(HttpServletRequest request) {
		Monitor monitor = (Monitor)request.
				getSession().
				getServletContext().
				getAttribute(ContextListener.MONITOR);
		logger.debug("handling stop request with monitor: {}", monitor);
		monitor.stop();
		return monitor.getStatus();
	}
}
