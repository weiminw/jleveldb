package com.google.leveldb.utils;

import static com.google.leveldb.LogConstants.HEADER_SIZE;










import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.leveldb.LogChunkType;
import com.google.leveldb.SizeOf;
import com.google.leveldb.WriteBatch;
import com.google.leveldb.WriteBatch.Handler;

public final class Slices {
	private static final Logger logger = LogManager.getLogger(Slices.class);

	public static Slice allocateSlice(byte[] raw) {
		return Slice.of(raw);
	}
	public static Slice allocateSlice(int capicity) {
		return Slice.of(new byte[capicity]);
	}

	public static Slice newLogHeaderSlice(int crc, int sliceLength, LogChunkType type){
		Slice slice = Slice.create(HEADER_SIZE);
		SliceDataOutputStream sliceOutput = slice.getOutputStream();
		try {
			sliceOutput.writeInt(crc);
			sliceOutput.writeByte((byte) (sliceLength & 0xff));
			sliceOutput.writeByte((byte) (sliceLength >>> 8));
			sliceOutput.writeByte((byte) (type.getTypeValue()));
			return slice;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Slice newLogContentSlice(long sequenceBegin,WriteBatch writeBatch) {
		Slice slice = Slice.create(SizeOf.SIZE_OF_LONG+SizeOf.SIZE_OF_INT+writeBatch.getApproximateSize());
		final SliceDataOutputStream sliceOutput = slice.getOutputStream();
		try {
			sliceOutput.writeLong(sequenceBegin);
			sliceOutput.writeInt(writeBatch.size());
			writeBatch.iterate(new Handler(){
				@Override
				public void put(Slice key, Slice value) {
					// TODO Auto-generated method stub
					try {
						sliceOutput.writeInt(key.length());
						sliceOutput.write(key.getRawBytes());
						sliceOutput.writeInt(value.length());;
						sliceOutput.write(value.getRawBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				@Override
				public void delete(Slice key) {
					// TODO Auto-generated method stub
					
				}
				
				private byte[] makeRecord(int type,byte[] key, byte[] value){
					return null;
				}
			});
			return slice;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
