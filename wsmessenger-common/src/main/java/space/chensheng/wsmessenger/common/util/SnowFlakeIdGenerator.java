package space.chensheng.wsmessenger.common.util;

/**
 * 
 * Generate long type ID. The composition of id is 1bit-41bit-10bit-12bit. 
 * The 1bit is 0 which means to generate positive id value. 
 * The 41bit is the diff between current time millis and id generating start time millis.
 * The 10bit is the machine id.
 * The 12bit is the sequence.
 *
 */
public class SnowFlakeIdGenerator {
	private final long twepoch;
	
	private final long machineId;
	
	private long sequence = 0L;
	
	private final long machineIdBits = 10L;
	
	private final long sequenceBits = 12L;
	
	private final long maxMachineId = -1L ^ (-1L << machineIdBits);
	
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);
	
	private final long machineIdShift = sequenceBits;
	
	private final long elapsedTimeMillisShift = machineIdBits + sequenceBits;

	private long lastElapsedTimeMillis = -1L;
	
	public SnowFlakeIdGenerator(long machineId, long twepoch) {
		if (machineId < 0) {
			throw new IllegalArgumentException("machineId must not be negative");
		}
		
		if (machineId > maxMachineId) {
			throw new IllegalArgumentException("machineId must be less than " + (maxMachineId + 1));
		}
		
		if (twepoch > System.currentTimeMillis()) {
            throw new IllegalArgumentException("twepoch must not be greater than current time milliseconds");
		}
		
		this.machineId = maxMachineId;
		this.twepoch  = twepoch ;
	}
	
	public synchronized long nextId() {
		long elapsedTimeMillis = getElapsedTimeMillis();
		if (elapsedTimeMillis < lastElapsedTimeMillis) {
			throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d elapsed milliseconds", elapsedTimeMillis));
		}

        if (elapsedTimeMillis == lastElapsedTimeMillis) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                elapsedTimeMillis = tillNextElapsedTime();
            }
        } else {
            sequence = 0L;
        }

        lastElapsedTimeMillis = elapsedTimeMillis;
		
		return (elapsedTimeMillis << elapsedTimeMillisShift) | (machineId << machineIdShift) | sequence;
	}
	
	private long getElapsedTimeMillis() {
		return System.currentTimeMillis() - twepoch;
	}

    private long tillNextElapsedTime() {
	    long nextElapsedTimeMillis = getElapsedTimeMillis();
	    while (nextElapsedTimeMillis <= lastElapsedTimeMillis) {
	        nextElapsedTimeMillis = getElapsedTimeMillis();
	    }
	    return nextElapsedTimeMillis;
	}
}
