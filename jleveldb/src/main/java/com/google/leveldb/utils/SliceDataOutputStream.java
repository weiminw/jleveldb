
package com.google.leveldb.utils;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.leveldb.utils.Slice;

/**
 * 该类不是线程安全的
 * @author william.wangwm
 *
 */
public class SliceDataOutputStream extends OutputStream implements DataOutput {

	private final Slice slice;
	private int position = 0;
	private Logger logger = LogManager.getLogger(SliceDataOutputStream.class);

	public SliceDataOutputStream(Slice slice) {
		// TODO Auto-generated constructor stub
		this.slice = slice;
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeByte(int v) throws IOException {
		this.write(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		
	}

	@Override
	public void writeChar(int v) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeInt(int v) throws IOException {
		// TODO Auto-generated method stub
		this.write((v >>> 24) & 0xFF);
        this.write((v >>> 16) & 0xFF);
        this.write((v >>>  8) & 0xFF);
        this.write((v >>>  0) & 0xFF);
	}

	@Override
	public void writeLong(long v) throws IOException {
		// TODO Auto-generated method stub
		this.write((byte)(v >>> 56) & 0xFF);
        this.write((byte)(v >>> 48) & 0xFF);
        this.write((byte)(v >>> 40) & 0xFF);
        this.write((byte)(v >>> 32) & 0xFF);
        this.write((byte)(v >>> 24) & 0xFF);
        this.write((byte)(v >>> 16) & 0xFF);
        this.write((byte)(v >>>  8) & 0xFF);
        this.write((byte)(v >>>  0) & 0xFF);
        
	}

	@Override
	public void writeFloat(float v) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeDouble(double v) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBytes(String s) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChars(String s) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUTF(String s) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		Preconditions.checkPositionIndex(this.position+1, this.slice.length());
		this.slice.getRawBytes()[this.position] = (byte)b;
		this.position++;
	}

}
