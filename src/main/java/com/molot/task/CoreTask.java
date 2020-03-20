package com.molot.task;

public abstract class CoreTask {
	private boolean executed;
	
	public void process() {
		executed = true;
	}
	
	public abstract void run();
	
	public boolean isExecuted() {
		return executed;
	}
	
}
