package space.chensheng.wsmessenger.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class PcInfoUtil {
	public static String getOs() {
		return System.getProperty("os.name");
	}
	
	public static boolean isWindows() {
		String os = getOs();
		if(os != null && os.toLowerCase().indexOf("win") >= 0 ) {
			return true;
		}
		return false;
	}
	
	public static boolean isLinux() {
		String os = getOs();
		if(os != null && os.toLowerCase().indexOf("linux") >= 0 ) {
			return true;
		}
		return false;
	}
	
	public static boolean isPortAvail(int port) {
		try {
			Socket socket = new Socket("localhost", port);
			socket.close();
			return false;
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
		return true;
	}
	
	public static String getMacAddr() {
		StringBuilder sb = new StringBuilder();
		try {
			InetAddress inetAddr = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddr);
			if (ni != null) {
				byte[] mac = ni.getHardwareAddress();
				if (mac != null) {
					for(int i=0; i<mac.length; i++) {
						if(i != 0) {
							sb.append("-");
						}
						int temp = mac[i]&0xff;
						String str = Integer.toHexString(temp);
						if(str.length()==1) {
							sb.append("0"+str);
						}else {
							sb.append(str);
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return sb.toString().toUpperCase();
	}
	
	public static int getUsedMemory() {
		if (isWindows()) {
			int usedMemory = 0;
			List<String> result = CmdUtil.executeAndWaitFor2("tasklist /FO LIST", 2000);
			if (result != null) {
				for (String str : result) {
					if (str.contains("K") || str.contains("k")) {
						int used = StringUtil.parseToInt(str.replaceAll("[^0-9]+", ""), 0);
						usedMemory += used;
					}
				}
			}
			return usedMemory;
		} else if (isLinux()) {
			int usedMemory = 0;
			List<String> result = CmdUtil.executeAndWaitFor2("cat /proc/meminfo", 2000);
			if (result != null) {
				int total = 0;
				int free = 0;
				for (String str : result) {
					if (str.toLowerCase().contains("memtotal")) {
						total = StringUtil.parseToInt(str.replaceAll("[^0-9]+", ""), 0);
					} else if (str.toLowerCase().contains("memfree")) {
						free = StringUtil.parseToInt(str.replaceAll("[^0-9]+", ""), 0);
					}
				}
				usedMemory = total - free;
			}
			return usedMemory;
		}
	    
		return -1;
	}
}
