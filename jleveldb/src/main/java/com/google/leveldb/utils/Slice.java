package com.google.leveldb.utils;

import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.leveldb.LogChunkType;

import static com.google.leveldb.LogConstants.HEADER_SIZE;

public final class Slice {
	private ImmutableList<Byte> rawData;
	private int offset = 0;
	private final int length;

	private Slice(byte[] data) {
		this.rawData = ImmutableList.copyOf(Bytes.asList(data));
		this.length = this.rawData.size();

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

	public byte[] getRawBytes() {
		return this.getBytes(0, length);
	}

	public byte[] getBytes() {
		return this.getBytes(this.offset, this.length);
	}

	public byte[] getBytes(int index, int length) {
		index += this.offset;
		Preconditions.checkPositionIndex(index, this.length);
		return Bytes.toArray(this.rawData.asList());
	}

	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}

	public static Builder builder(int capability) {
		// TODO Auto-generated method stub
		return new Builder(capability);
	}

}
