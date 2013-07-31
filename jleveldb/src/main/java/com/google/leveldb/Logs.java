package com.google.leveldb;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


public final class Logs {
	
	public static int getChunkCheckSum(int chunkTypeId, byte[] buffer, int offset, int length){
		///
		
		Checksum crc32 = new CRC32();
		crc32.update(chunkTypeId);
		crc32.update(buffer, offset, length);
		return (int) crc32.getValue();
		
	}
}
