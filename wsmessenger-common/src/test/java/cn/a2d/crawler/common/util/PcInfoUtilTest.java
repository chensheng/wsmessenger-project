package cn.a2d.crawler.common.util;

import junit.framework.TestCase;
import space.chensheng.wsmessenger.common.util.PcInfoUtil;

public class PcInfoUtilTest extends TestCase {
	public void testGetOs() {
		String os = PcInfoUtil.getOs();
		assertNotNull(os);
		System.out.println(os);
	}
	
	public void testGetMacAddr() {
		String mac = PcInfoUtil.getMacAddr();
		assertNotNull(mac);
		System.out.println(mac);
	}
	
	public void testIsPortAvail() {
		boolean result = PcInfoUtil.isPortAvail(2050);
		System.out.println("port is avail " + result);
	}
}
