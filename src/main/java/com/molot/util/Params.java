package com.molot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.molot.common.utils.ByteUtils;
import com.molot.util.speed.SpeedConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Params {
	private String filename;
	private Map<String, String> items = new HashMap<>();
	
	public static final Params SETTINGS = getInner("settings");
	
//	public static final Params WORK_MODES = getInner("work_modes");
	
	public static final Params PROTOCOL = getInner("protocol");
	
	/**
	 * Змінні, які зберігаються лише під час роботи програми
	 */
	public static final Params RUNTIME = new Params();
	

	
	/**
	 * Параметри Ethernet
	 */
	public static final Params ETHERNET = new Params();


	
	public static final SpeedConfig SPEED = SpeedConfig.getInternal();
	
	public static Params getInner(String name) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("data/" + name + ".json"), "UTF-8");
			String jsonStr = "";
			while(sc.hasNextLine()) {
				jsonStr += sc.nextLine();
				jsonStr += "\n";
			}
			sc.close();
			Params result = new Gson().fromJson(jsonStr, Params.class);
			result.filename = name;
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
		
		return null;
	}
	
	public void save() {
		if (filename != null) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String result = gson.toJson(this);
			try {
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/" + filename + ".json"), "UTF-8"));
				try {
					out.write(result);
				} finally {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getInt(String name, int defValue) {
		if (items.containsKey(name)) {
			return Integer.parseInt(items.get(name));
		} else {
			return defValue;
		}
	}
	
	public int getInt(String name) {
		return Integer.parseInt(items.get(name));
	}

	public boolean exists(String param) {
		return items.containsKey(param);
	}
	
	public byte getByte(String name) {
		return ByteUtils.getLastByteFromInt(getInt(name));
	}
	
	public String getString(String name) {
		return items.get(name);
	}
	
	public String getKeyByValue(String value) {
		for(String key: items.keySet()) {
			String tValue = items.get(key);
			if (tValue.equals(value)) {
				return key;
			}
		}
		
		return null;
	}
	
	public boolean getBoolean(String name) {
		String value = items.get(name);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(items.get(name));
	}
	
	public void putInt(String name, int value) {
		items.put(name, value + "");
	}
	
	public void putString(String name, String value) {
		items.put(name, value);
	}
	
	public void putBoolean(String name, boolean value) {
		items.put(name, value + "");
	}

	public String getString(String key, String defValue) {
		if (items.containsKey(key)) {
			return getString(key);
		} else {
			return defValue;
		}
	}
}
