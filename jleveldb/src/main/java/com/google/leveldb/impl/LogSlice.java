package com.google.leveldb.impl;

import com.google.leveldb.Slice;

public class LogSlice implements Slice {
	private byte[] data = null;
	private final int offset;
	private final int length;

	public LogSlice(String data) {
		// TODO Auto-generated constructor stub
		this.data = data.getBytes();
		this.offset = 0;
		this.length = data.length();
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return this.data;
	}

}
