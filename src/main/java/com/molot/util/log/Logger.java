package com.molot.util.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logger {
	private Map<String, List<String>> logs = new HashMap<String, List<String>>();
	private List<String> tagsForPrint = new ArrayList<String>();
	
	private LogListener logListener;
	
	public Logger() {
		//printMessagesWithTag("КА передачі файлів");
		//printMessagesWithTag("КА прийому файлів");
		//printMessagesWithTag("Обробник пакетів");
//		printMessagesWithTag("Ядро");
	}
	
	public void printMessagesWithTag(String tag) {
		tagsForPrint.add(tag);
	}
	
	public void log(String text) {
		log("default", text);
	}
	
	public void log(String tag, String text) {
		if (logListener != null) {
			logListener.log(tag, text);
		}
		
		getLogForTag(tag).add(text);
		
		if (tagsForPrint.contains(tag)) {
			System.out.println(tag + ": " + text);
		}
	}
	
	private List<String> getLogForTag(String tag) {
		List<String> result = logs.get(tag);
		if (result == null) {
			result = new ArrayList<String>();
			logs.put(tag, result);
		}
		return result;
	}
	
	public List<String> getTagsForPrint() {
		return tagsForPrint;
	}
	
	public void setLogListener(LogListener logListener) {
		this.logListener = logListener;
	}
}
