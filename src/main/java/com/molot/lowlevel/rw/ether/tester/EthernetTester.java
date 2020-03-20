package com.molot.lowlevel.rw.ether.tester;

import com.molot.util.Params;

import java.net.InetSocketAddress;
import java.net.Socket;

public class EthernetTester {
	private EthernetTestListener listener;
	
	public EthernetTester(EthernetTestListener listener) {
		this.listener = listener;
	}
	
	public void test(final String ip, final int port) {
		new Thread() {
			public void run() {
				
				Socket socket = new Socket();
				try {
					socket.connect(new InetSocketAddress(ip, port), Params.SETTINGS.getInt("ethernet-connect-timeout"));
					socket.getOutputStream().write(Params.PROTOCOL.getInt("ethernet-reset-socket"));
					socket.getOutputStream().flush();
					
					socket.close();
					
					listener.success();
				} catch (Exception e) {
					e.printStackTrace();
					listener.fail();
				} finally {
					if (socket != null) {
						try {
							socket.close();
							socket = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}.start();
	}
}
