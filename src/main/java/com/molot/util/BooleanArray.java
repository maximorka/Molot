package com.molot.util;

public class BooleanArray {
	private boolean[] data = new boolean[1000];
	private int headIndex;
	
	public BooleanArray(int size) {
		data = new boolean[size];
	}
	
	public boolean[] getLastBits(int count) {
		boolean[] result = new boolean[count];
		int offset = size() - count;
		for(int i = 0; i < count; i++) {
			result[i] = data[offset + i];
		}
		return result;
	}
	
	public BooleanArray copy() {
		BooleanArray result = new BooleanArray(headIndex + 1);
		for(int i = 0; i < headIndex; i++) {
			result.data[i] = data[i];
		}
		result.headIndex = headIndex;
		return result;
	}
	
	public void set(int index, boolean value) {
		data[index] = value;
	}
	
	public BooleanArray removeLast(int count) {
		headIndex -= count;
		if (headIndex < 0) {
			headIndex = 0;
		}
		return this;
	}
	
	public BooleanArray() {
		this(1000);
	}
	
	public void clear() {
		headIndex = 0;
	}
	
	public void add(boolean[] items) {
		for(int i = 0; i < items.length; i++) {
			add(items[i]);
		}
	}
	
	public void add(boolean item) {
		if (headIndex >= data.length - 1) {
			setMaxSize(data.length * 2);
		}
		
		data[headIndex++] = item;
	}
	
	public boolean get(int index) {
		return data[index];
	}
	
	public int size() {
		return headIndex;
	}
	
	public void print() {
		for(int i = 0; i < headIndex; i++) {
			System.out.print(data[i] ? "1" : "0");
		}
	}
	
	public void insertAsFirst(boolean item) {
		if (size() + 1 >= data.length) {
			setMaxSize(Math.max(headIndex, 1) * 2);
		}
		if (headIndex > 0) {
			System.arraycopy(data, 0, data, 1, headIndex);
		}
		data[0] = item;
		headIndex++;
	}
	
	public void removeFirst() {
		System.arraycopy(data, 1, data, 0, headIndex);
		headIndex--;
	}
	
	public void removeFirst(int count) {
		System.arraycopy(data, count, data, 0, headIndex);
		headIndex -= count;
	}
	
	public boolean[] getBits() {
		boolean[] result = new boolean[headIndex];
		
		System.arraycopy(data, 0, result, 0, headIndex);
		return result;
	}

	public void setMaxSize(int bufferSize) {
		boolean[] newData = new boolean[bufferSize];
		int copySize = Math.min(newData.length, data.length);
		System.arraycopy(data, 0, newData, 0, copySize);
		data = newData;
	}
	
	public static void main(String[] args) {
		BooleanArray a = new BooleanArray();
		a.add(ByteUtils.getBits("1110011"));
        a.removeByIndex(0);
        a.insertAfter(1, false);
		a.print();
	}

    public void insertAfterAndShiftLeft(int i, boolean b) {

    }

    public void insertAfter(int i, boolean b) {
        boolean[] before = new boolean[i];
        System.arraycopy(data, 0, before, 0, before.length);

        boolean[] after = new boolean[headIndex - i];
        System.arraycopy(data, i, after, 0, after.length);

        System.arraycopy(before, 0, data, 0, before.length);
        System.arraycopy(after, 0, data, i + 1, after.length);

        data[i + 1] = b;
        headIndex++;
    }

    public void removeByIndex(int i) {
		boolean[] before = new boolean[i];
        System.arraycopy(data, 0, before, 0, before.length);

        boolean[] after = new boolean[headIndex - i - 1];
        System.arraycopy(data, i + 1, after, 0, after.length);

        System.arraycopy(before, 0, data, 0, before.length);
        System.arraycopy(after, 0, data, i, after.length);

        headIndex--;
	}

}
