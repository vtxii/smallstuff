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

package com.vtxii.smallstuff.etl.fileuploader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vtxii.smallstuff.etl.common.Processor;
import com.vtxii.smallstuff.etl.common.LandingManager;

@RestController
public class FileUploaderController {

	private static final Logger logger = LoggerFactory
			.getLogger(FileUploaderController.class);

	@Autowired
	private Processor processor;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {
		if (false == file.isEmpty()) {
			try {
				// See if the file exists. If it does then another user
				// has uploaded a file of the same name at the same time
				// and it has not yet been processed.
				File newFile = new File(name);
				if (true == newFile.exists()) {
					String msg = "fail: file \"" + name
							+ "\" exists and hasn't been processed";
					logger.error(msg);
					return msg;
				}

				// Write the file (this should be to the landing directory)
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(newFile));
				stream.write(bytes);
				stream.close();

				// Process the file
				LandingManager.process(newFile, processor, null);

				return "success: loaded " + name;
			} catch (Exception e) {
				String msg = "fail: file \"" + name + "\" with error "
						+ e.getMessage();
				logger.error(msg);
				return msg;
			}
		} else {
			return "fail: file \"" + name + "\" empty";
		}
	}
}
