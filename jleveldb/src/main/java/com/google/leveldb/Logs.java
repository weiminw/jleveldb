package com.google.leveldb;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class Logs {
	public static int getChunkCheckSum(){
		///
		Checksum crc32 = new CRC32();
		crc32.update(b);
		return (int) crc32.getValue();
		
	}
}
