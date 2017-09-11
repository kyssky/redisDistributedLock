package com.redis;

public interface JedisLock {
	public void lock();
	public void unlock();
}
