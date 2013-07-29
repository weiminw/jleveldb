package com.google.leveldb.impl;


import java.awt.List;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.leveldb.LogChunkType;
import com.google.leveldb.LogWriter;
import com.google.leveldb.utils.Slice;

import static com.google.leveldb.LogConstants.BLOCK_SIZE;
import static com.google.leveldb.LogConstants.HEADER_SIZE;
public class MMapLogWriter implements LogWriter {
	private final static int PAGE_SIZE = 1024 * 1024;
	private final FileChannel fileChannel;
	private final AtomicBoolean closed = new AtomicBoolean();
	private MappedByteBuffer mappedByteBuffer;
	/**
     * Current offset in the current block
     */
    private int blockOffset;
	public MMapLogWriter(File file, long fileNumber) throws IOException {
		Preconditions.checkNotNull(file, "file is null");
        Preconditions.checkArgument(fileNumber >= 0, "fileNumber is negative");
		this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
		mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 0, PAGE_SIZE);
	}

	@Override
	public void addRecord(Slice record) throws IOException {
		// TODO Auto-generated method stub
		// used to track first, middle and last blocks
        boolean begin = true;
        SliceDataInputStream sliceInput = new SliceDataInputStream(record);
        System.out.println(sliceInput.available());
        do {
            int bytesRemainingInBlock = BLOCK_SIZE - blockOffset;
            Preconditions.checkState(bytesRemainingInBlock >= 0);
            if (bytesRemainingInBlock < HEADER_SIZE ){
            	this.mappedByteBuffer.put(new byte[bytesRemainingInBlock]);
            	blockOffset = 0;
                bytesRemainingInBlock = BLOCK_SIZE - blockOffset;
            }
            
            int bytesAvailableInBlock = bytesRemainingInBlock - HEADER_SIZE;
            Preconditions.checkState(bytesAvailableInBlock >= 0);
            boolean end = false;
            int fragmentLength = 0;
            if(record.length()>bytesAvailableInBlock){
            	end = false;
            	fragmentLength = bytesAvailableInBlock;
            }
            else {
            	end = false;
            	fragmentLength = record.length();
            }
            // determine block type
            LogChunkType type;
            if (begin && end) {
                type = LogChunkType.FULL;
            }
            else if (begin) {
                type = LogChunkType.FIRST;
            }
            else if (end) {
                type = LogChunkType.LAST;
            }
            else {
                type = LogChunkType.MIDDLE;
            }
            // write log chunk;
            byte[] chunck = new byte[fragmentLength];
            sliceInput.readFully(chunck);
            writeLogChunk(type, chunck);
            begin = false;
        }
        while(sliceInput.available()>0);
        sliceInput.close();
            
	}

	private void writeLogChunk(LogChunkType type, byte[] chunck) {
		// TODO Auto-generated method stub
		System.out.println(String.valueOf(chunck));
		this.mappedByteBuffer.put(chunck);
		
		
	}
	
	private Slice createLogRecordHeader(LogChunkType type, Slice slice)
    {
        int crc = getChunkChecksum(type.getTypeValue(), slice, slice.getRawOffset(), slice.length());
        return Slice.newLogHeaderSlice(crc, slice,type);
    }
	
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		LogWriter logWriter = new  MMapLogWriter(new File("/home/weiminw/test.txt"),1);
		logWriter.addRecord(new LogSlice("111111111"));
	}

}
