package com.molot.common.collection;

import com.molot.common.utils.ByteUtils;

public class BooleanQueue {
	private boolean[] data;
	
	private int start, end;
	private volatile boolean locked;
	
	public BooleanQueue(int maxSize) {
		data = new boolean[maxSize];
	}
	
	public void add(boolean value) {
		locked = true;
		if (end >= data.length - 1) {
			setMaxSize(data.length * 2);
		}
		data[end++] = value;
		locked = false;
	}
	
	public int size() {
		if (locked) {
			return 0;
		}
		
		return end - start;
	}
	
	public boolean poll() {
		locked = true;
			boolean result = data[start];
			start++;
			
			if (start == end) {
				clear();
			}
		locked = false;
		return result;
	}
	
	public void clear() {
		start = 0;
		end = 0;
	}
	
	public void print() {
		boolean[] bits = new boolean[size()];
		for(int i = 0; i < bits.length; i++) {
			bits[i] = poll();
		}
		
		ByteUtils.printBits(bits);
	}

	public void setMaxSize(int bufferSize) {
		boolean[] newData = new boolean[bufferSize];
		int copySize = Math.min(newData.length, data.length);
		System.arraycopy(data, 0, newData, 0, copySize);
		data = newData;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public static void main(String[] args) {
		BooleanQueue q = new BooleanQueue(1000);
		for(int i = 0; i < 1000; i++) {
			q.add(true);
		}
		//q.print();
		System.out.println("size: " + q.size());
		//System.out.println("s: " + q.size());
		
		System.out.println();
		int readCount = 0;
		while(q.size() > 0) {
			readCount++;
			System.out.print(q.poll() ? "1" : "0");
		}
		System.out.println();
		System.out.println("read count: " + readCount);
	}
}
