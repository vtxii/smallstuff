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
 * Filter
 * 
 * Interface for classes that will filter incoming files. 
 */
public interface Filter {

	/**
	 * Takes an incoming file, expressed by the absolute path, and determines if
	 * it should be processed, returning null if it is not, or returning a path to
	 * another file, e.g., get a trigger file and returns the path of the associated 
	 * file.
	 * 
	 * @param path absolute path of the file to be filtered
	 * @throws Exception
	 */
	public Path process(Path path) throws Exception;
}
