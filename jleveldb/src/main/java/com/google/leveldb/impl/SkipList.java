package com.google.leveldb.impl;

public class SkipList {
	class Node {
		int key;
		
		public Node(int key){
			this.key = key;
		}
	}
	class Index {
		Node node;
		Index right;
		Index down;
		public Index(Node node,Index right,Index down){
			this.node = node;
			this.right = right;
			this.down = down;
		}
	}
	
	private Index head = null;
	
	public SkipList(){
		this.head = new Index(new Node(Integer.MIN_VALUE),null,null);
		
	}
	public Index search(int key){
//		Index _indexNode = this.head;
//		Index _preIndexNode = this.head;
//		do {
//			do{
//				if(key > _indexNode.node.key){
//					if(_indexNode.right!=null){
//						_preIndexNode = _indexNode; 
//						_indexNode = _indexNode.right;
//					}
//				}
//			}
//			while(_indexNode.right!=null);
//			
//			_indexNode = _indexNode.down;
//			
//		}
//		while(_indexNode!=null);
//		return _preIndexNode;
		
		Index _currentNode = this.head;
		
		while(_currentNode.right!=null && key >= _currentNode.right.node.key ){
			_currentNode = _currentNode.right;
		}
		return _currentNode;
	}
	
	public boolean insert(int key){
		Index left = this.search(key);
		if (left.node.key == key){
			return false;
		}
		else {
			Node newNode = new Node(key);
			Index l1IndexNode = new Index(newNode,left.right,null);
			left.right = l1IndexNode;
			return true;
		}
		
		
	}
	public static void main(String args[]){
		SkipList list = new SkipList();
		assert list.search(1) == list.head:"not head";
		list.insert(1);
		list.insert(2);
		System.out.println(list.search(2).node.key);
	}
	
}
