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

import java.nio.file.Path;

import com.vtxii.smallstuff.etl.common.Processor;

public class DefaultProcessor implements Processor {

	@Override
	public void process(Path path) throws Exception {
		System.out.println("Got a file: " + path.toFile().getName());
	}
}
