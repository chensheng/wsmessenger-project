package space.chensheng.wsmessenger.common.component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.common.util.ExceptionUtil;
import space.chensheng.wsmessenger.common.util.JsonMapper;
import space.chensheng.wsmessenger.common.util.StringUtil;


public abstract class MessengerContext {
	private static final Logger logger = LoggerFactory.getLogger(MessengerContext.class);
	
	private int maxPendingMsg;
	
	private int waitingMsgTimoutMillis;
	
	private int waitingMsgMaxSize;
	
	public MessengerContext(String defaultConfPath, String customerConfPath) {
		if (defaultConfPath != null) {
			loadContext(defaultConfPath);
		}
		
		if (customerConfPath != null) {
			loadContext(customerConfPath);
		}
		
		checkNotNull(MessengerContext.class);
		checkNotNull(getClass());
	}
	
	protected void loadContext(String confPath) {
		Properties props = new Properties();
		InputStream propsIs = null;
		try {
			propsIs = this.getClass().getResourceAsStream(confPath);
			if(propsIs == null) {
				return;
			}
			props.load(propsIs);
			fillFields(props, MessengerContext.class);
			fillFields(props, getClass());
		} catch (IOException e) {
			logger.error(ExceptionUtil.getExceptionDetails(e));
		} finally {
			if(propsIs != null) {
				try {
					propsIs.close();
				} catch (IOException e) {
					logger.error(ExceptionUtil.getExceptionDetails(e));
				}
			}
		}
	}
	
	private void fillFields(Properties props, Class<?> clzz) {
		Field[] fields = clzz.getDeclaredFields();
		for(Field field : fields) {
			if(Modifier.isStatic(field.getModifiers())){
				continue;
			}
			
			field.setAccessible(true);
			PropOption propOption = field.getAnnotation(PropOption.class);
			
			if (propOption != null && propOption.ignore()) {
				continue;
			}
			
			String fieldName = field.getName();
			String propKey = fieldName;
			String propVal = props.getProperty(propKey);
			if(propVal == null){
				continue;
			}
			
			Class<?> fieldType = field.getType();
			Object fieldVal = null;
			
			if(fieldType == Integer.class || fieldType == int.class) {
				fieldVal = StringUtil.parseToInt(propVal, 0);
			} else if(fieldType == Long.class || fieldType == long.class) {
				fieldVal = StringUtil.parseToLong(propVal, 0);
			} else if(fieldType == Boolean.class || fieldType == boolean.class) {
				fieldVal = Boolean.valueOf(propVal);
			} else if(fieldType == String.class) {
				fieldVal = propVal;
			} else {
				continue;
			}
			
			try {
				field.set(this, fieldVal);
			} catch (IllegalArgumentException e) {
				logger.error(ExceptionUtil.getExceptionDetails(e));
			} catch (IllegalAccessException e) {
				logger.error(ExceptionUtil.getExceptionDetails(e));
			}
		}
	}
	
	private void checkNotNull(Class<?> clzz) {
		Field[] fields = clzz.getDeclaredFields();
		for(Field field : fields) {
			if(Modifier.isStatic(field.getModifiers())){
				continue;
			}
			
			field.setAccessible(true);
			PropOption propOption = field.getAnnotation(PropOption.class);
			
			if (propOption != null && propOption.notNull()) {
				try {
					if (field.get(this) == null) {
						throw new NullPointerException(field.getName() + " may not be null");
					}
				} catch (IllegalArgumentException e) {
					logger.error(ExceptionUtil.getExceptionDetails(e));
				} catch (IllegalAccessException e) {
					logger.error(ExceptionUtil.getExceptionDetails(e));
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return JsonMapper.nonEmptyMapper().toJson(this);
	}
	
	public int getMaxPendingMsg() {
		return maxPendingMsg;
	}

	public int getWaitingMsgTimoutMillis() {
		return waitingMsgTimoutMillis;
	}

	public int getWaitingMsgMaxSize() {
		return waitingMsgMaxSize;
	}
}
