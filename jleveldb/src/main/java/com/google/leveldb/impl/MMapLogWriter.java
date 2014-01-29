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
import com.google.common.io.Files;
import com.google.leveldb.LogChunkType;
import com.google.leveldb.LogWriter;
import com.google.leveldb.utils.Slice;
import com.google.leveldb.utils.SliceDataInputStream;
import com.google.leveldb.utils.Slices;
import com.google.leveldb.Logs;

import static com.google.leveldb.LogConstants.BLOCK_SIZE;
import static com.google.leveldb.LogConstants.HEADER_SIZE;
/**
 * 基于内存映射方式写Log
 * @author william.wangwm
 *
 */
public class MMapLogWriter implements LogWriter {
	private Logger logger = LogManager.getLogger(MMapLogWriter.class);
	/*
	 * 每次从LOG文件映射到内存的大小。ldb源码采用小于1M的时候，翻倍扩展的方式，32K,64K,128K...1M
	 */
	private final static int PAGE_SIZE = 1<<20; // 1M
	/*
	 * 从日志文件映射到内存的Buffer.每次大小为1M
	 */
	private MappedByteBuffer mappedByteBuffer; 
	private final AtomicBoolean closed = new AtomicBoolean();
	private final FileChannel fileChannel;
	/*
	 * 映射内存时，映射起始位置对应于日志文件的位置.
	 */
	private int fileOffset = 0;
	/**
     * 没条日志记录在日志BLOCK中的偏移量. 每个block为 32K
     */
    private int blockOffset = 0;
    /**
     * 构造函数.每次执行都会有一个fileNumber, fileNumber决定了最新的log文件。这个数字在MF维护
     * @param file
     * @param fileNumber
     * @throws IOException
     */
	public MMapLogWriter(File file, long fileNumber) throws IOException {
		Preconditions.checkNotNull(file, "file is null");
        Preconditions.checkArgument(fileNumber >= 0, "fileNumber is negative");
		this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
		mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, fileOffset, PAGE_SIZE);
	}
	
	/**
	 * 写入记录到日志系统。
	 * 假设记录很大，每次从slice 里面读取不超过32K 的数据，然后写到处理后写到log中。
	 */
	@Override
	public void addRecord(Slice record) throws IOException {
		// used to track first, middle and last blocks
        boolean begin = true;
        SliceDataInputStream sliceInput = record.getInputStream();
        do {
            int bytesRemainingInBlock = BLOCK_SIZE - blockOffset; // 计算当前block剩下的字节
            Preconditions.checkState(bytesRemainingInBlock >= 0); 
            if (bytesRemainingInBlock < HEADER_SIZE ){ // 如果当前block剩下的字节小于header_size = 7,则新开一个block,当前剩下字节填0
            	this.mappedByteBuffer.put(new byte[bytesRemainingInBlock]);// 这种填充方法不安全.
            	blockOffset = 0;
                bytesRemainingInBlock = BLOCK_SIZE - blockOffset;
            }
            int bytesAvailableInBlock = bytesRemainingInBlock - HEADER_SIZE; // 重新计算当前block可用字段
            Preconditions.checkState(bytesAvailableInBlock >= 0);
            boolean end = false;
            int fragmentLength = 0;
            
            if(sliceInput.available()>bytesAvailableInBlock){// 判断记录尚未读取的数据长度 > 当前block可用长度，则当前block不会结束
            	end = false;
            	fragmentLength = bytesAvailableInBlock;
            }
            else {// 判断记录尚未读取的数据长度 <= 当前block可用长度，则当前block结束
            	end = true;
            	fragmentLength = sliceInput.available();
            }
            /*
             * 确定Recored Header段 Type的取值
             */
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
		Slice headerSlice = this.createLogRecordChunkHeader(type, chunckSlice);
		this.mappedByteBuffer.put(headerSlice.getBytes());
		this.mappedByteBuffer.put(chunckSlice.getBytes());
	}
	
	private Slice createLogRecordChunkHeader(LogChunkType type, Slice slice)
    {
		int crc = Logs.getChunkCheckSum(type.getTypeValue(), slice.getBytes(), slice.getRawOffset(), slice.length());
		logger.debug(crc);
        return Slices.newLogHeaderSlice(crc, slice.length(),type);
    }
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		LogWriter logWriter = new  MMapLogWriter(new File("d:/home/weiminw/test.txt"),1);
		Slice s = Slices.allocateLogSlice("abc".getBytes());
		
		logWriter.addRecord(s);
	}

}
