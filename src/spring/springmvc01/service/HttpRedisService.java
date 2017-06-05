package spring.springmvc01.service;

import spring.springmvc01.bean.RedisPan;

public interface HttpRedisService{
	void redissave(RedisPan pan);
	RedisPan redisget(int id);
}
