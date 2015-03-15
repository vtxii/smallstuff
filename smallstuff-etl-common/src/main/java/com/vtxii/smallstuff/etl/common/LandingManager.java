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

package com.vtxii.smallstuff.etl.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LandingManager
 * 
 * This class is intended to support both the fileupload and the filewatcher
 * projects, It coordinated the processing of file.
 */
public class LandingManager {

	private static final Logger logger = LoggerFactory.getLogger(LandingManager.class);
	
	private static long LOCK_POLLING_INTERVAL = 100; 
	private static String DEFAULT_WORKING_DIR = "working";
	private static String DEFAULT_ARCHIVE_DIR = "archive";
	
	/**
	 * Processes a landed file: see if this file is to be processed by invoking the
	 * invoking the optional Filter object (the Filter object takes the incoming 
	 * path but may return a different path or just null); wait on a lock to ensure 
	 * it is not still being landed; move it to the working directory, adding a 
	 * timestamp extension, and invoking the provided Processor to perform application 
	 * specific processing; finally moving the file to the archive and compressing it.  
	 * The state of a file is expressed by the three directories: landing(optional), 
	 * working(required), and archived(required). 
	 *    
	 * @param file recently landed file
	 * @param processor object that implements the Processor interface
	 * @param filter object that implements the Filter interface
	 * @throws Exception 
	 */
	public static void process(File file, Processor processor, Filter filter) throws Exception {
		logger.debug("args: {}, {}, {}", file, processor, filter);
		
		// First things first - get the timestamp.  Then the path
		// associated with the file.
		long timestamp = System.currentTimeMillis();
		Path path = Paths.get(file.getAbsolutePath());
		logger.debug("timestamp: {}", timestamp);

		// See if we are even processing this file
		if (null != filter) {
			try {
				path = filter.process(path);
				if (null == path) {
					return;
				}
			} catch (Exception e) {
				logger.error("filter exception: {}", e);
				throw e;
			}
		}
		
		// Loop waiting for the file to be closed
		while (false == isClosed(path)) {
			try {
				logger.debug("{} is busy", path);
				Thread.sleep(LOCK_POLLING_INTERVAL);
			} catch (InterruptedException e) {
				logger.error("sleep exception: {}", e);
				throw e;
			}
		}
		
		// Process the file: add a timestamp to the file, move it to the 
		// working directory, process it, move it to archive directory, and zip it;
		// then cleanup the lock.
		Path working = null;
		Path archive = null;
		try {
			// Move the file and release and close the lock
			working = editPath(path, DEFAULT_WORKING_DIR, file.getName(), String.valueOf(timestamp));
			Files.move(path, working);
			logger.debug("working path: {}", working);

			if (null != processor) {
				try {
					processor.process(working);
				} catch (Exception e) {
					logger.error("processor exception: {}", e);
					throw e;
				}
			}
			
			// Move to the archive, zip, and delete
			archive = editPath(working, DEFAULT_ARCHIVE_DIR, file.getName(), String.valueOf(timestamp));
			logger.debug("archiv path: {}", archive);
			Files.move(working, archive);
			zip(archive);
			Files.delete(archive);
		} catch (IOException e) {
			logger.error("io exception: {}, {}, {}, {}", path, working, archive, e);
			throw e;
		}
	}
	
	/**
	 * Compress the file specified by the path
	 * 
	 * @param path
	 * @throws IOException
	 */
	private static void zip(Path path) throws IOException {
		// Define the zip file
		Path zip = Paths.get(path.toString() + ".zip");
		File out = zip.toFile();
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(out));
		zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
		
		// Add a file to the zip file
		zipOut.putNextEntry(new ZipEntry(path.toFile().getName()));
		FileInputStream fileIn = new FileInputStream(path.toFile());
		byte[] buffer = new byte[4096];
		int byteCount = 0;
		while (-1 !=(byteCount = fileIn.read(buffer))) {
			zipOut.write(buffer, 0, byteCount);
		}
		
		// Cleanup
		fileIn.close();
		zipOut.closeEntry();
		zipOut.flush();
		zipOut.close();
	}
	
	/**
	 * Creates a new path from a starting path by replacing the directory, the file,
	 * and the extension, e.g., ../../directory/file.extension
	 * 
	 * @param starting the path that is going to be modified
	 * @param directory new directory
	 * @param file new file
	 * @param extension new extension
	 * @return
	 */
	private static Path editPath(Path starting, String directory, String file, String extension) {
		int length = starting.getNameCount();
		Path path = Paths.get(
				starting.getRoot().toString(),
				starting.subpath(0, length-2).toString(), 
				directory, 
				file + "." + extension);	
		return path;
	}
	
	/**
	 * Really NON PORTABLE way to check if a file is being used by another process.
	 * No, the NIO stuff doesn't work!
	 * 
	 * @param path absolute path of the file of interest
	 * @return true if it is good to go
	 * @throws Exception 
	 */
	private static boolean isClosed(Path path) throws Exception {
		Process plsof = null;
		BufferedReader reader = null;
	    try {
	        plsof = new ProcessBuilder(new String[]{"lsof", "|", "grep", path.toString()}).start();
	        reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
	        String line;
	        while((line=reader.readLine())!=null) {
	            if(line.contains(path.toString())) {                            
	                reader.close();
	                plsof.destroy();
	                return false;
	            }
	        }
		    reader.close();
		    plsof.destroy();
	    } catch(Exception e) {
			logger.error("isClosed exception: {}", e);
			throw e;
	    } finally {
		    try {
				reader.close();
			} catch (IOException e) {
				logger.error("closing reader exception: {}", e);
				throw e;
			}
		    plsof.destroy();
	    }
	    return true;
	}
}
