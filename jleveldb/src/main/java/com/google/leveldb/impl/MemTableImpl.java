package com.google.leveldb.impl;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.leveldb.MemTable;
import com.google.leveldb.ValueType;
import com.google.leveldb.WriteBatch.Handler;
import com.google.leveldb.utils.Slice;

public class MemTableImpl implements MemTable {
	private static final Logger logger = LogManager.getLogger(MemTableImpl.class);
	private final ConcurrentSkipListMap<InternalKey, Slice> table;
	public static final class WriteBathAdd2MemTableHandler implements Handler{
		private final MemTable memTable;
		private long sequence = 0;
		public WriteBathAdd2MemTableHandler(MemTable memTable, long sequence) {
			super();
			this.memTable = memTable;
			this.sequence = sequence;
		}
		@Override
		public void put(Slice key, Slice value) {
			this.memTable.add(sequence++, key, value);
			logger.debug(key);
		}
		@Override
		public void delete(Slice key) {
			// TODO Auto-generated method stub
			this.memTable.add(sequence++, key, null);
		}
		
		
	}
	public MemTableImpl() {
		table = new ConcurrentSkipListMap<InternalKey, Slice>(new InternalKeyComparator());
	}
	@Override
	public void add(long sequence, Slice key, Slice value) {
		InternalKey internalKey = new InternalKey(key,sequence,ValueType.VALUE);
		this.table.put(internalKey, value);
	}
	@Override
	public void remove(long sequence, Slice key) {
		InternalKey internalKey = new InternalKey(key,sequence,ValueType.DELETION);
		this.table.put(internalKey, null);
	}
	


}
