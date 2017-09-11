package com.redis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;



public class RedisLock implements JedisLock{
	private Jedis thejedis=null;
	private String status;
	
	public RedisLock(Jedis thejedis,String status) {
		// TODO Auto-generated constructor stub
		this.status=status;
		this.thejedis=thejedis;
	}
	@Override
	public void lock() {
		// TODO Auto-generated method stub
		while (thejedis.setnx(this.status, "ehhe") == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//设置超时时间
		thejedis.expire(this.status, 1000);
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		Transaction transaction=null;
		try {
		thejedis.watch(this.status);
		transaction =
				thejedis.multi();
		thejedis.del(this.status);
		thejedis.unwatch();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(transaction!=null) {
				try {
					transaction.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (thejedis!=null) {
				thejedis.close();
			}
		}
	}
	
}

