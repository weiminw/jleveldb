package com.google.leveldb.impl;

public class VersionSet {

	private long lastSequence;

	public long getLastSequence() {
		return this.lastSequence;
	}

	public void setLastSequence(long lastSequence) {
		this.lastSequence = lastSequence;
	}

}
