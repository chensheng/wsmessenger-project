package space.chensheng.wsmessenger.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdUtil {
	
	public static String executeAndWaitFor(String cmd, int timeoutMillis) {
		StringBuilder result = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			
			new Thread(new StreamGobbler(process.getErrorStream(), timeoutMillis, result)).start();
			new Thread(new StreamGobbler(process.getInputStream(), timeoutMillis, result)).start();;
			
			process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (IOException e) {
			result.append(e.toString());
		} catch (InterruptedException e) {
			result.append(e.toString());
		}
		return result.toString();
	}
	
	public static List<String> executeAndWaitFor2(String cmd, int timeoutMillis) {
		List<String> result = new ArrayList<String>();
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			
			new Thread(new StreamGobbler(process.getErrorStream(), timeoutMillis, result)).start();
			new Thread(new StreamGobbler(process.getInputStream(), timeoutMillis, result)).start();;
			
			process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (IOException e) {
			result.add(e.toString());
		} catch (InterruptedException e) {
			result.add(e.toString());
		}
		return result;
	}
	
	private static class StreamGobbler implements Runnable{
		
        private InputStream is;
        
        private long timeoutMillis = 1000;
        
        private StringBuilder resultCollector;
        
        private List<String> resultList;
        
        public StreamGobbler(InputStream is, long timeoutMillis, StringBuilder resultCollector) {
        	this.is = is;
        	this.timeoutMillis = timeoutMillis;
        	this.resultCollector = resultCollector;
        }
        
        public StreamGobbler(InputStream is, long timeoutMillis, List<String> resultList) {
        	this.is = is;
        	this.timeoutMillis = timeoutMillis;
        	this.resultList = resultList;
        }
        
		@Override
		public void run() {
			InputStreamReader isReader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isReader);
			
			long startTime = System.currentTimeMillis();
			String line = null;
			try {
				while ((line=br.readLine()) != null) {
					if (System.currentTimeMillis() - startTime > timeoutMillis ) {
						collectResult("read stream timeout");
						break;
					}
					collectResult(line);
				}
			} catch (IOException e) {
				collectResult(e.toString());
			}
		}
		
		private void collectResult(String data) {
			if (resultCollector != null) {
				resultCollector.append(data);
			}
			
			if (resultList != null) {
				resultList.add(data);
			}
		}
	}
}
