package com.google.leveldb;

import java.io.IOException;

import com.google.leveldb.utils.Slice;

public interface LogWriter {

	void addRecord(Slice record) throws IOException;
}
