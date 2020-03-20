package com.molot.util.speed;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SpeedConfig {
	public Map<String, SpeedItem> items;
	private int speed = 100;
	
	public int getMode() {
		return items.get(Integer.toString(speed)).mode;
	}
//
//	public String getDescription() {
//		return items.get(Integer.toString(speed)).description;
//	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
//	public List<Integer> getPossibleSpeeds() {
//		List<Integer> result = new ArrayList<>();
//		for(String key: items.keySet()) {
//			result.add(Integer.parseInt(key));
//		}
//		Collections.sort(result);
//		return result;
//	}
//
//	public Integer[] getPossibleSpeedsAsArray() {
//		List<Integer> tmp = getPossibleSpeeds();
//		Integer[] result = new Integer[tmp.size()];
//		for(int i = 0; i < tmp.size(); i++) {
//			result[i] = tmp.get(i);
//		}
//		return result;
//	}
	
	public static SpeedConfig getInternal() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("data/speed.json"), "UTF-8");
			String jsonStr = "";
			while(sc.hasNextLine()) {
				jsonStr += sc.nextLine();
				jsonStr += "\n";
			}
			sc.close();
			SpeedConfig result = new Gson().fromJson(jsonStr, SpeedConfig.class);
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
	

	
	public static void main(String[] args) {
		SpeedConfig c = SpeedConfig.getInternal();
		//c.setSpeed(1000);
		//System.out.println(c.getDescription());
		
	}
}
