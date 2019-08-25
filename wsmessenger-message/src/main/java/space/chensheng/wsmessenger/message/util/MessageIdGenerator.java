package space.chensheng.wsmessenger.message.util;

import space.chensheng.wsmessenger.common.util.SnowFlakeIdGenerator;

public class MessageIdGenerator {

	private static final SnowFlakeIdGenerator snowFlakeIdGenerator = new SnowFlakeIdGenerator(1, 1493737860828L);
	
	public static long generate() {
	    return snowFlakeIdGenerator.nextId();
	}
}
