package com.google.leveldb.impl;


import java.util.concurrent.locks.ReentrantLock;
import com.google.leveldb.SizeOf;
import com.google.leveldb.DB;
import com.google.leveldb.LogWriter;
import com.google.leveldb.WriteBatch;
import com.google.leveldb.utils.Slice;
import com.google.leveldb.utils.Slices;

public class DBImpl implements DB {
	LogWriter logWriter = null;
	private final ReentrantLock mutex = new ReentrantLock();
	public DBImpl(){
		
	}
	@Override
	public boolean put(byte[] key, byte[] value) {
		return false;
	}
	
	protected void writeInternal(WriteBatch updates){
		// check background
		mutex.lock();
		if(updates.size()>0){
			// TODO …Í«Îƒ⁄¥Ê makeRoomForWrite
			final long sequenceBegin = 1;
			final long sequenceEnd = 1;
			Slice logRecord = createLogRecord(updates,sequenceBegin);
		}
		
	}
	
	private Slice createLogRecord(WriteBatch updates, long sequenceBegin) {
		Slice record = Slices.allocate(SizeOf.SIZE_OF_LONG + SizeOf.SIZE_OF_INT + updates.getApproximateSize());
		
		return null;
	}
	public static void main(String[] args) {
		DB db = new DBImpl();
		db.put("test".getBytes(), "test".getBytes());
	}

	
	
}
