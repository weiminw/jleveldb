package com.google.leveldb.impl;

import java.util.concurrent.locks.ReentrantLock;

import com.google.leveldb.DB;
import com.google.leveldb.LogWriter;

public class DBImpl implements DB {
	LogWriter logWriter = null;
	private final ReentrantLock mutex = new ReentrantLock();
	public DBImpl(){
		
	}
	@Override
	public boolean put(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void writeInternal(){
		// check background
		mutex.lock();
	}

	
	
}
