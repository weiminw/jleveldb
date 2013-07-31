package com.google.leveldb.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.leveldb.LogChunkType;

import static com.google.leveldb.LogConstants.HEADER_SIZE;

public final class Slice {
	private Logger logger = LogManager.getLogger(Slice.class);
	private ImmutableList<Byte> rawData;
	private int length;
	private int offset;

	private Slice(byte[] data) {
		this.rawData = ImmutableList.copyOf(Bytes.asList(data));
		this.length = this.rawData.size();

	}
	private Slice(ImmutableList<Byte> data,int offset, int fragmentLength){
		this.rawData = data;
		this.offset = offset;
		this.length = fragmentLength;
	}

	public static Slice newLogHeaderSlice(int crc, Slice slice, LogChunkType type) {
		ByteArrayDataOutput sliceOutput = ByteStreams
				.newDataOutput(HEADER_SIZE);
		sliceOutput.writeInt(crc);
		sliceOutput.writeByte((byte) (slice.length() & 0xff));
		sliceOutput.writeByte((byte) (slice.length() >>> 8));
		sliceOutput.writeByte((byte) (type.getTypeValue()));
		return new Slice(sliceOutput.toByteArray());

	}

	public static Slice of(final String data) {
		return new Slice(data.getBytes());
	}

	public static Slice of(byte[] data) {
		return new Slice(data);
	}
	



	public byte[] getBytes() {
		return this.getBytes(0, this.length);
	}

	public byte[] getBytes(int index, int length) {
		index += this.offset;
		Preconditions.checkPositionIndex(index, this.rawData.size());
		return Bytes.toArray(this.rawData.asList());
	}

	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}

	public byte[] getRawBytes() {
		// TODO Auto-generated method stub
		return Bytes.toArray(this.rawData);
	}

	public int getRawOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}

	public Slice slice(int offset,int fragmentLength) {
		// TODO Auto-generated method stub
		return new Slice(this.rawData,offset,fragmentLength);
	}



}
