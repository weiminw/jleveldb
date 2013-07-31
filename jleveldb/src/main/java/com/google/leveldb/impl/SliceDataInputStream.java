package com.google.leveldb.impl;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.leveldb.utils.Slice;

public final class SliceDataInputStream extends InputStream implements
		DataInput {
	private final Slice slice;
	private int position = 0;
	private Logger logger = LogManager.getLogger(SliceDataInputStream.class);

	public SliceDataInputStream(Slice slice) {
		// TODO Auto-generated constructor stub
		this.slice = slice;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		for (int i = 0; i < b.length; i++) {
			b[i] = slice.getBytes()[this.position++];
		}
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int skipBytes(int n) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean readBoolean() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte readByte() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readShort() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUnsignedShort() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char readChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readInt() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readLong() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloat() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double readDouble() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readUTF() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return this.slice.length() - this.position;
	}

	public Slice readSlice(int fragmentLength) {
		// TODO Auto-generated method stub
		Slice slice = this.slice.slice(this.position, fragmentLength);
		this.position += fragmentLength;
		return slice;

	}

}
