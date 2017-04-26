package space.chensheng.wsmessenger.message.util;

import java.util.Random;

public class MessageIdGenerator {
	private static final Random random = new Random();
	
	public static long generate() {
		long id = System.currentTimeMillis() + random.nextInt(10000);
		return id;
	}
}
