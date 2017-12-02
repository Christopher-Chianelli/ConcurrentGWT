package com.gmail.chianelli.chris.tasks;

public interface Task<R> {
	boolean resume();
	R getResult();
}
