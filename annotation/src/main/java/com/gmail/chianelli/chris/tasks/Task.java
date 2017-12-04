package com.gmail.chianelli.chris.tasks;

public interface Task<T> {
	public boolean resume();
	public T getReturnValue();
}
