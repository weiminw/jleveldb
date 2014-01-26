package com.google.leveldb;
import java.io.Closeable;
public interface WriteBatch extends Closeable {

    public WriteBatch put(byte[] key, byte[] value);
    public WriteBatch delete(byte[] key);
    public int size();
	public int getApproximateSize();
}
