package com.google.leveldb;

public interface Slice {

	int length();
	
	byte[] getBytes();

}
