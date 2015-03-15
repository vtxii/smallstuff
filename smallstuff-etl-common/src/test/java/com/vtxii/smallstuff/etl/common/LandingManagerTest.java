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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LandingManagerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test() {
		try {
			// Create test folder with standard children folders 
			File testFolder = folder.newFolder("test");
			File landingFolder = Paths.get(testFolder.getAbsolutePath(), "landing").toFile();
			landingFolder.mkdir();
			File workingFolder = Paths.get(testFolder.getAbsolutePath(), "working").toFile();
			workingFolder.mkdir();
			File archiveFolder = Paths.get(testFolder.getAbsolutePath(), "archive").toFile();
			archiveFolder.mkdir();
			
			// Create test file
			File testFile = Paths.get(landingFolder.getAbsolutePath(), "test_file").toFile();
			testFile.createNewFile();
			
			// Run the manager
			LandingManager.process(testFile, null, null);

			// Check that the landing and working folders are empty and the 
			// archive folder has one file
			assert(0 == landingFolder.listFiles().length);
			assert(0 == workingFolder.listFiles().length);
			assert(1 == archiveFolder.listFiles().length);

		} catch (Exception e) {
			fail("with exception: " + e);
		}
	}

}
