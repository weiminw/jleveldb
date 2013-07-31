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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.leveldb.LogChunkType;
import com.google.leveldb.LogWriter;
import com.google.leveldb.utils.Slice;
import com.google.leveldb.Logs;

import static com.google.leveldb.LogConstants.BLOCK_SIZE;
import static com.google.leveldb.LogConstants.HEADER_SIZE;
public class MMapLogWriter implements LogWriter {
	private final static int PAGE_SIZE = 1024 * 1024;
	private final FileChannel fileChannel;
	private final AtomicBoolean closed = new AtomicBoolean();
	private MappedByteBuffer mappedByteBuffer;
	private Logger logger = LogManager.getLogger(MMapLogWriter.class);
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
            
            if(sliceInput.available()>bytesAvailableInBlock){
            	end = false;
            	fragmentLength = bytesAvailableInBlock;
            }
            else {
            	end = false;
            	fragmentLength = sliceInput.available();
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
            Slice chunkSlice = sliceInput.readSlice(fragmentLength);
            writeLogChunk(type, chunkSlice);
            this.blockOffset = HEADER_SIZE+chunkSlice.length();
            begin = false;
        }
        while(sliceInput.available()>0);
        sliceInput.close();
            
	}

	private void writeLogChunk(LogChunkType type, Slice chunckSlice) {
		// TODO Auto-generated method stub
		Slice headerSlice = this.createLogRecordChunkHeader(type, chunckSlice);
		this.mappedByteBuffer.put(headerSlice.getBytes());
		this.mappedByteBuffer.put(chunckSlice.getBytes());
		
		
	}
	
	private Slice createLogRecordChunkHeader(LogChunkType type, Slice slice)
    {
		int crc = Logs.getChunkCheckSum(type.getTypeValue(), slice.getRawBytes(), slice.getRawOffset(), slice.length());
        return Slice.newLogHeaderSlice(crc, slice,type);
    }
	
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		LogWriter logWriter = new  MMapLogWriter(new File("/home/weiminw/test.txt"),1);
		File file = new File("/home/weiminw/velocity.log");
		
		logWriter.addRecord(Slice.of(Joiner.on(',').join(Files.readLines(file, Charsets.UTF_8))));
	}

}
