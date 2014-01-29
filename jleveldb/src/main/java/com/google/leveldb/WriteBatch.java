package com.google.leveldb;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.leveldb.utils.Slice;

public interface WriteBatch extends Closeable {

    public WriteBatch put(Slice key,Slice value);
    public WriteBatch delete(Slice key);
    public int size();
	public int getApproximateSize();
	public interface Handler {
		public void put(Slice key, Slice value);
		public void delete(Slice key);
	}
	public void iterate(Handler handler);
}
