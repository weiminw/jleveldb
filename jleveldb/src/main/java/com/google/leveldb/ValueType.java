package com.google.leveldb;

public enum ValueType {
	DELETION(0x00),
    VALUE(0x01);
	
	private int type;
	ValueType(int type){
		this.type = type;
	}
	public int get() {
		// TODO Auto-generated method stub
		return this.type;
	}
	
}
