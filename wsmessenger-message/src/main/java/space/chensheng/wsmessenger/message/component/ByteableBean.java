package space.chensheng.wsmessenger.message.component;

import space.chensheng.wsmessenger.message.util.ByteReflectUtil;
import space.chensheng.wsmessenger.message.util.ByteReflectUtil.ReflectResult;

public abstract class ByteableBean {
	protected byte[] bytes;
	
	/**
	 * Set all fields value from bytes 
	 * @param bytes
	 * @return left bytes after all fields set.
	 */
	public byte[] fromBytes(byte[] bytes) {
		ReflectResult result = ByteReflectUtil.fromBytes(this, bytes);
		if (result.convertedBytes != null) {
			bytes = result.convertedBytes;
		}
		return result.unconvertedBytes;
	}
	
	/**
	 * Convert all fields value to bytes
	 * @return bytes stand for this object
	 */
	public byte[] toBytes() {
		if (bytes ==null) {
			bytes = ByteReflectUtil.toBytes(this);
		}
		return bytes;
	}
	
	@Override
	public String toString() {
		return ByteReflectUtil.formatToString(this);
	}
}
