package com.google.leveldb.impl;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import com.google.leveldb.MemTable;
import com.google.leveldb.SizeOf;
import com.google.leveldb.DB;
import com.google.leveldb.LogWriter;
import com.google.leveldb.WriteBatch;
import com.google.leveldb.utils.Slice;
import com.google.leveldb.utils.Slices;

public class DBImpl implements DB {
	private LogWriter logWriter;
	private final ReentrantLock mutex = new ReentrantLock();
	private MemTable memTable = new MemTableImpl();
	private long sequence = 0; /*@TODO sequence 统一管理*/
	public DBImpl(){
		try {
			this.logWriter = new  MMapLogWriter(new File("d:/home/weiminw/test.txt"),1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.logWriter = null;
			e.printStackTrace();
		}
	}
	@Override
	public boolean put(byte[] key, byte[] value) {
		WriteBatch batch = new WriteBatchImpl();
		batch.put(Slice.of(key), Slice.of(value));
		this.writeInternal(batch);
		return true;
	}
	
	protected void writeInternal(WriteBatch updates){
		// check background
		mutex.lock();
		if(updates.size()>0){
			// TODO 判断memtable有空间可以被写入
			// 更新sequence
			final long sequenceBegin = sequence;
			final long sequenceEnd = sequence+updates.size();
			this.sequence = sequenceEnd;
			// 生成log record 并记录到log中
			Slice record = Slices.newLogContentSlice(sequenceBegin, updates);
			try {
				this.logWriter.addRecord(record);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 写入memtable;
			updates.iterate(new MemTableImpl.WriteBathAdd2MemTableHandler(this.memTable,sequenceBegin));
		}
		
	}
	
	public static void main(String[] args) {
		DB db = new DBImpl();
		db.put("test".getBytes(), "test".getBytes());
		db.put("test".getBytes(), "test".getBytes());
	}

	
	
}
