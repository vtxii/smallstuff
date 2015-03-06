package com.vtxii.smallstuff.etl.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class LandingManagerTest {

	@Test
	public void test() {
		Path from = Paths.get("/home/devuser/Downloads/ui-bootstrap-tpls-0.12.0.min.js");
		Path to = Paths.get("/home/devuser/test10/landing/b_test_file");
		try {
			Files.copy(from, to);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LandingManager manager = new LandingManager();
		manager.process(to.toFile(), null, null);
	}

}
