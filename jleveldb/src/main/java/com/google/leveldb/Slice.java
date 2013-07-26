package com.google.leveldb;

import java.util.Arrays;

import com.google.common.base.Preconditions;

public final class Slice {
	private byte[] rawData;
	private int offsetOfRawData = 0;
	private final int  length;
	
	public Slice(byte[] data){
		this.rawData = data;
		this.length = this.rawData.length;
		
	}
	
	public static Slice newSlice(int capacity){
		return new Slice(new byte[capacity]);
	}
	
	public static Slice of(String data){
		return new Slice(data.getBytes());
	}
	
	public byte[] getBytes(){
		return this.getBytes(0, this.length);
	}
	
	public byte[] getBytes(int index,int length){
		index += this.offsetOfRawData;
		Preconditions.checkPositionIndex(index, this.length);
		if(index==0){
			return Arrays.copyOf(rawData, length);
		}
		byte[] coppiedData = new byte[length];
		System.arraycopy(rawData, index, coppiedData, 0, length);
		return coppiedData;
	}

	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}
	
	

}
