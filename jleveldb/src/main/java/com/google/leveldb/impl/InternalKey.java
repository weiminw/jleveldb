package com.google.leveldb.impl;

import java.io.IOException;

import com.google.leveldb.SizeOf;
import com.google.leveldb.ValueType;
import com.google.leveldb.utils.Slice;
import com.google.leveldb.utils.SliceDataOutputStream;
import com.google.leveldb.utils.Slices;

public final class InternalKey {
	private final Slice userKey;
	private final long sequenceNum;
	private final ValueType valueType;
	
	public InternalKey(Slice userKey,long sequenceNum,ValueType valueType){
		this.userKey = userKey;
		this.sequenceNum = sequenceNum;
		this.valueType = valueType;
	}
	/*
	 * | User key (string) | sequence number (7 bytes) | value type (1 byte) |
	 */
	public Slice encode(){
		Slice internalKeySlice = Slices.allocateSlice(this.userKey.length()+SizeOf.SIZE_OF_LONG);
		SliceDataOutputStream output = internalKeySlice.getOutputStream();
		try {
			output.write(userKey.getBytes());
			long sequenceAndType = this.sequenceNum << 8 | this.valueType.get();
			output.writeLong(sequenceAndType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return internalKeySlice;
	}
}
