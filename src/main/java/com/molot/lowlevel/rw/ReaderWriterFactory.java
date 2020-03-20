package com.molot.lowlevel.rw;

import com.molot.lowlevel.rw.ether.EthernetReaderWriter;

//import ui.settings.device.InitDialogSettings;
//import ui.settings.device.logic.DeviceType;

public class ReaderWriterFactory {
	private static ReaderWriterFactory instance = new ReaderWriterFactory();
	private ReaderWriter readerWriter;
	
	private ReaderWriterFactory() {
		
	}
	
	public static ReaderWriterFactory getInstance() {
		return instance;
	}
	
	public ReaderWriter getReaderWriter() {
		close();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initReaderWriter();
		return readerWriter;
	}
	
	private void initReaderWriter() {

			initSingleReaderWriter();

	}
	
	private void initSingleReaderWriter() {
		readerWriter = createReaderWriterByConfig();
	}
	
//	private void initMultipleReaderWriter(InitDialogSettings sets) {
//		ReaderWriter output = createReaderWriterByConfig(sets.getFirstDeviceSettings());
//		ReaderWriter input = createReaderWriterByConfig(sets.getSecondDeviceSettings());
//		readerWriter = new MultiReaderWriter(input, output);
//	}
	
	public ReaderWriter createReaderWriterByConfig() {
		//String deviceName = config.get("device-name");
		//System.out.println("Device name: "+deviceName);
//		if (deviceName.equals(DeviceType.ups.getDeviceName())) {
			return createUPSReaderWriter();
//		} else if (deviceName.equals(DeviceType.winradio.getDeviceName())) {
//			return createWinradioReaderWriter(config);
//		} else if (deviceName.equals(DeviceType.icom.getDeviceName())) {
//			return createIcomReaderWriter(config);
//		} else if (deviceName.equals(DeviceType.stub.getDeviceName())) {
//			return new StubReaderWriter();
//		}
		
		//throw new IllegalArgumentException("Can't create ReaderWriter for " + deviceName);
	}

//	private ReaderWriter createIcomReaderWriter(Map<String, String> config) {
//		return new IcomReaderWriter(config);
//	}
	
//	private ReaderWriter createWinradioReaderWriter(Map<String, String> config) {
//		return new WinradioReaderWriter();
//	}
//
	public ReaderWriter createUPSReaderWriter() {

			String ip = "192.168.0.2";
			int port = 80;
			
			return new EthernetReaderWriter(ip, port);

	}
	
	public void close() {
		System.out.println("Call Close() on ReaderWriterFactory");
		if (readerWriter != null) {
			readerWriter.close();
//			readerWriter = null;
		}
	}
}
