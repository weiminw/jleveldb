package com.google.leveldb;

import java.io.IOException;

public interface LogWriter {

	void addRecord(Slice record) throws IOException;
}
