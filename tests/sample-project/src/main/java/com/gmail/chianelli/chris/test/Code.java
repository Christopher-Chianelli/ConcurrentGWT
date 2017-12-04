package com.gmail.chianelli.chris.test;

import com.gmail.chianelli.chris.annotation.ConcurrentClass;
import com.gmail.chianelli.chris.annotation.ConcurrentMethod;

@ConcurrentClass(name = Code.class)
public class Code {
	@ConcurrentMethod
	public void test() {
		
	}
}
