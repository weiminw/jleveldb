package com.google.leveldb.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.junit.Test;

public class TestMMMap {
	
	@Test
	public void test() throws IOException, InterruptedException {
		FileChannel fileChannel = new RandomAccessFile(new File("d:/a.wwm"), "rw").getChannel();
		MappedByteBuffer mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 0, 1024);// 打开 1K
		while (mappedByteBuffer.remaining() > "test".getBytes().length ){
			System.out.println("---------------1--");
			mappedByteBuffer.put("test".getBytes());
		}
		mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 1024, 1024); // 打开另外1K
		while (mappedByteBuffer.remaining() > "test".getBytes().length ){
			System.out.println("---------------2--");
			mappedByteBuffer.put("test".getBytes());
		}
		mappedByteBuffer = null;
		System.gc(); 
		while(true){
			Thread.currentThread().sleep(2000);
		}
	}

}
