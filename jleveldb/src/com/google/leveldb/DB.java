package com.google.leveldb;

public interface DB {
	public boolean put(byte[] key, byte[] value);
}
