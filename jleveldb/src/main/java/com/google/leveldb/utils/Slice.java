package com.google.leveldb.utils;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;


public final class Slice {
	private Logger logger = LogManager.getLogger(Slice.class);
	private final byte[] rawData;
	private final int length;
	private final int offset;

	private Slice(byte[] data) {
		this.rawData = data;
		this.length = data.length;
		this.offset = 0;
	}
	private Slice(byte[] data,int offset, int fragmentLength){
		this.rawData = data;
		this.offset = offset;
		this.length = fragmentLength;
	}

	public static Slice of(byte[] data) {
		return new Slice(data);
	}
//	public static Slice of(ByteArrayDataOutput data){
//		return new Slice(data.toByteArray());
//	}
	
	public static Slice create(int capability){
		return new Slice(new byte[capability]);
	}


	public byte[] getBytes() {
		return this.getBytes(0, this.length);
	}

	public byte[] getBytes(int index, int length) {
		index += this.offset;
		Preconditions.checkPositionIndex(index, this.rawData.length);
		return Arrays.copyOfRange(rawData, index,length);
	}

	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}

	/**
	 * 该方法只提供给SliceDataInputStream,
	 * @return
	 */
	byte[] getRawBytes() {
		return this.rawData;
	}

	public int getRawOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}

	public Slice slice(int offset,int fragmentLength) {
		// TODO Auto-generated method stub
		return new Slice(this.rawData,offset,fragmentLength);
	}
	
	public SliceDataInputStream getInputStream(){
		return new SliceDataInputStream(this);
	}

	public SliceDataOutputStream getOutputStream(){
		return new SliceDataOutputStream(this);
	}


}
