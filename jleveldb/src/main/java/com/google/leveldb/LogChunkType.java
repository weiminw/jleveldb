package com.google.leveldb;

public  enum LogChunkType {
	EMPTY(0),
    FIRST(1),
    MIDDLE(2),
    LAST(3),
    FULL(4);
    int typeValue = 0;
    LogChunkType(int value){
		this.typeValue = value;
	}
    public int getTypeValue(){
    	return this.typeValue;
    }
    
}
