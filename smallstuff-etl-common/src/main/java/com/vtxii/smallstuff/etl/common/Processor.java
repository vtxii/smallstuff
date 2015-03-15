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

import java.nio.file.Path;

/**
 * Processor
 * 
 * Wrapper the application specific processing.
 *
 */
public interface Processor {

	/**
	 * Invokes application specific processing, e.g. staging, DQ, etc.
	 * 
	 * @param path absolute path of the file to be processed
	 * @throws Exception
	 */
	public void process(Path path) throws Exception;
}
