package space.chensheng.wsmessenger.common.util;

import java.util.Random;

public class RandomUtil {
	private static final Random RANDOM = new Random();
	
	public static void repeatTaskRandomly(RandomTask task, int min, int max) {
		int count = RANDOM.nextInt(max-min+1) + min;
		for (int i = 0; i < count; i++) {
			task.run();
		}
	}
	
	public static int nextInt(int bound) {
		return RANDOM.nextInt(bound);
	}
	
	public static int nextInt(int min, int max) {
		return RANDOM.nextInt(max-min+1) + min;
	}
	
	public static boolean nextBoolean() {
		return  RANDOM.nextBoolean();
	}
	
	public static interface RandomTask {
		void run();
	}
}
