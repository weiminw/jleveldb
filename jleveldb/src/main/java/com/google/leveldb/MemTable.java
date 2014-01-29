package com.google.leveldb;

import com.google.leveldb.utils.Slice;

public interface MemTable {
	public void add(long sequence,Slice key,Slice value);
	public void remove(long sequence,Slice key);
}
