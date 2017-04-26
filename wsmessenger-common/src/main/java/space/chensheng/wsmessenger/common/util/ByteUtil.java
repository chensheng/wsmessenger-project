package space.chensheng.wsmessenger.common.util;

public class ByteUtil {
	public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
}
