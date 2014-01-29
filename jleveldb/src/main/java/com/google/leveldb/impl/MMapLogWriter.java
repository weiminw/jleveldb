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
 * �����ڴ�ӳ�䷽ʽдLog
 * @author william.wangwm
 *
 */
public class MMapLogWriter implements LogWriter {
	private Logger logger = LogManager.getLogger(MMapLogWriter.class);
	/*
	 * ÿ�δ�LOG�ļ�ӳ�䵽�ڴ�Ĵ�С��ldbԴ�����С��1M��ʱ�򣬷�����չ�ķ�ʽ��32K,64K,128K...1M
	 */
	private final static int PAGE_SIZE = 1<<20; // 1M
	/*
	 * ����־�ļ�ӳ�䵽�ڴ��Buffer.ÿ�δ�СΪ1M
	 */
	private MappedByteBuffer mappedByteBuffer; 
	private final AtomicBoolean closed = new AtomicBoolean();
	private final FileChannel fileChannel;
	/*
	 * ӳ���ڴ�ʱ��ӳ����ʼλ�ö�Ӧ����־�ļ���λ��.
	 */
	private int fileOffset = 0;
	/**
     * û����־��¼����־BLOCK�е�ƫ����. ÿ��blockΪ 32K
     */
    private int blockOffset = 0;
    /**
     * ���캯��.ÿ��ִ�ж�����һ��fileNumber, fileNumber���������µ�log�ļ������������MFά��
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
	 * д���¼����־ϵͳ��
	 * �����¼�ܴ�ÿ�δ�slice �����ȡ������32K �����ݣ�Ȼ��д�������д��log�С�
	 */
	@Override
	public void addRecord(Slice record) throws IOException {
		// used to track first, middle and last blocks
        boolean begin = true;
        SliceDataInputStream sliceInput = record.getInputStream();
        do {
            int bytesRemainingInBlock = BLOCK_SIZE - blockOffset; // ���㵱ǰblockʣ�µ��ֽ�
            Preconditions.checkState(bytesRemainingInBlock >= 0); 
            if (bytesRemainingInBlock < HEADER_SIZE ){ // �����ǰblockʣ�µ��ֽ�С��header_size = 7,���¿�һ��block,��ǰʣ���ֽ���0
            	this.mappedByteBuffer.put(new byte[bytesRemainingInBlock]);// ������䷽������ȫ.
            	blockOffset = 0;
                bytesRemainingInBlock = BLOCK_SIZE - blockOffset;
            }
            int bytesAvailableInBlock = bytesRemainingInBlock - HEADER_SIZE; // ���¼��㵱ǰblock�����ֶ�
            Preconditions.checkState(bytesAvailableInBlock >= 0);
            boolean end = false;
            int fragmentLength = 0;
            
            if(sliceInput.available()>bytesAvailableInBlock){// �жϼ�¼��δ��ȡ�����ݳ��� > ��ǰblock���ó��ȣ���ǰblock�������
            	end = false;
            	fragmentLength = bytesAvailableInBlock;
            }
            else {// �жϼ�¼��δ��ȡ�����ݳ��� <= ��ǰblock���ó��ȣ���ǰblock����
            	end = true;
            	fragmentLength = sliceInput.available();
            }
            /*
             * ȷ��Recored Header�� Type��ȡֵ
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
