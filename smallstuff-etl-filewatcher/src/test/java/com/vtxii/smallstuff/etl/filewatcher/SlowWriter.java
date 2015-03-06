package com.vtxii.smallstuff.etl.filewatcher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SlowWriter {
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	public static void main(String[] args) {
		Path path = Paths.get(args[0]);
		int count = Integer.parseInt(args[1]);
		long sleep = Long.parseLong(args[2]);		
		String line = "hello world";
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	      for(int idx=0; idx<count; idx++){
	        writer.write(line);
	        Thread.sleep(sleep);
	      }
	    } catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
}
