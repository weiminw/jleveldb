package com.google.leveldb.impl;

import com.google.leveldb.DB;
import com.google.leveldb.LogWriter;

public class DBImpl implements DB {
	LogWriter logWriter = null;
	
	public DBImpl(){
		
	}
	@Override
	public boolean put(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void writeInternal(){
		
	}

	
	
}
