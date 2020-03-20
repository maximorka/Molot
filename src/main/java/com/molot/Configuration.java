package com.molot;

import com.molot.lowlevel.rw.ReaderWriter;

//import ua.com.integer.ui.main.MainUI;
//import ua.com.integer.com.molot.util.log.Logger;

public class Configuration {
	public String name;
	public ReaderWriter channel;
	//public Logger logger = new Logger();
	//public Stats stats = new Stats();
	public float timeBetweenUpdate = 0.1f;
	public int timeBetweenUpdateInMs = (int) (timeBetweenUpdate * 1000f);
	//public MainUI ui;
}
