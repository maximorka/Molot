package com.molot.lowlevel.rw.ether.tester;

public class HardConnectionTester implements EthernetTestListener {
	private int counter = -1;
	
	@Override
	public void success() {
		counter++;
		new EthernetTester(this).test("192.168.0.1", 80);
		
		System.out.println(counter);
	}

	@Override
	public void fail() {
		System.out.println("failed after " + counter + " retries!");
	}
	
	public static void main(String[] args) {
		new HardConnectionTester().success();
	}

}
