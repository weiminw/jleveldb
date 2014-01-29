package com.google.leveldb.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.leveldb.WriteBatch;
import com.google.leveldb.utils.Slice;

public class WriteBatchImpl implements WriteBatch {
	Map<Slice,Slice> data = Maps.newConcurrentMap(); // 
	private AtomicInteger approximate = new AtomicInteger(0);
	private static int HEADER_LENGTH = 12;
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.data.size();
	}

	@Override
	public int getApproximateSize() {
		// TODO Auto-generated method stub
		return this.approximate.get();
	}

	@Override
	public void iterate(Handler handler) {
		for(Slice key:this.data.keySet()){
			handler.put(key, this.data.get(key));
		}
	}

	@Override
	public WriteBatch put(Slice key, Slice value) {
		this.data.put(key, value);
		approximate.getAndAdd(HEADER_LENGTH+key.length()+value.length());
		return this;
	}

	@Override
	public WriteBatch delete(Slice key) {
		// TODO Auto-generated method stub
		this.data.put(key, null);
		approximate.getAndAdd(HEADER_LENGTH+key.length());
		return this;
	}



}
