package com.ramonli.lottery.core.support.dao;

public class ShardKeyContextHolder {
	public static final int ORACLE = 1;
	public static final int MYSQL = 2;
	private final static ThreadLocal<Integer> shardKeyContextHolder = new ThreadLocal<Integer>();
	
	public static Integer getShardKey(){
		return shardKeyContextHolder.get();
	}
	
	public static void setShardKey(Integer i){
		shardKeyContextHolder.set(i);
	}
}
