package space.chensheng.wsmessenger.message.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import space.chensheng.wsmessenger.common.util.ByteUtil;
import space.chensheng.wsmessenger.common.util.StringUtil;
import space.chensheng.wsmessenger.message.component.MessageOptions;

public class ByteReflectUtil {
	private static final Comparator<Field> fieldComparator = new Comparator<Field>() {

		@Override
		public int compare(Field o1, Field o2) {
			MessageOptions options1 = o1.getAnnotation(MessageOptions.class);
			MessageOptions options2 = o2.getAnnotation(MessageOptions.class);
			int order1 = options1 != null ? options1.order() : 0;
			int order2 = options2 != null ? options2.order() : 0;
			
			if (order1 > order2) {
				return 1;
			} else if (order1 < order2) {
				return  -1;
			} else {
				return o1.getName().compareTo(o2.getName());
			}
		}
	};
	
	
	public static ReflectResult fromBytes(Object targetObj, byte[] bytes) {
		ReflectResult result = new ReflectResult();
		
		if (targetObj == null) {
			return result;
		}
		
		if (bytes == null) {
			throw new NullPointerException("bytes may not be null");
		}
		
		Field[] fields = targetObj.getClass().getDeclaredFields();
		ByteBuf buf = Unpooled.copiedBuffer(bytes);
		if (fields != null) {
			Arrays.sort(fields, fieldComparator);
			for (int i=0; i<fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				String fieldName = field.getName();
				Class<?> fieldType = field.getType();
				Object fieldValue = null;
				
				MessageOptions options = field.getAnnotation(MessageOptions.class);
				boolean describeLastStrLen = options != null ? options.describeLastStrLen() : false;
				if (options != null && options.ignore()) {
					continue;
				}
				
				try {
					if (fieldType == Byte.class || fieldType == byte.class) {
						fieldValue = buf.readByte();
					} else if (fieldType == Boolean.class || fieldType == boolean.class) {
						fieldValue = buf.readBoolean();
					} else if (fieldType == Short.class || fieldType == short.class) {
						fieldValue = buf.readShort();
					} else if (fieldType == Integer.class || fieldType == int.class) {
						fieldValue = buf.readInt();
					} else if (fieldType == Long.class || fieldType == long.class) {
						fieldValue = buf.readLong();
					} else if (fieldType == Float.class || fieldType == float.class) {
						fieldValue = buf.readFloat();
					} else if (fieldType == Double.class || fieldType == double.class) {
						fieldValue = buf.readDouble();
					} else if (fieldType == String.class) {
						if (i == fields.length - 1 && !describeLastStrLen) {
							byte[] strBytes = new byte[buf.readableBytes()];
							buf.readBytes(strBytes);
							fieldValue = new String(strBytes, Charsets.UTF8);
						} else {
							int strLen = ByteUtil.toUnsignedInt(buf.readByte());
							byte[] strBytes = new byte[strLen];
							buf.readBytes(strBytes);
							fieldValue = new String(strBytes, Charsets.UTF8);
						}
					} else {
						throw new IllegalArgumentException("could not convert from bytes to " + fieldName + "[" +fieldType+"]");
					}
					
					field.set(targetObj, fieldValue);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		int convertedBytesLen = buf.readerIndex();
		if (convertedBytesLen > 0) {
			result.convertedBytes = new byte[convertedBytesLen];
			buf.getBytes(0, result.convertedBytes);
		}
		
		int unconvertedBytesLen = buf.readableBytes();
		if (unconvertedBytesLen > 0) {
			result.unconvertedBytes = new byte[unconvertedBytesLen];
			buf.readBytes(result.unconvertedBytes);
		}
		
		return result;
	}
	
	public static byte[] toBytes(Object targetObj) {
		byte[] resultBytes = null;
		if (targetObj != null) {
			Field[] fields = targetObj.getClass().getDeclaredFields();
			if (fields != null) {
				Arrays.sort(fields, fieldComparator);
				ByteBuf buf = Unpooled.buffer();
				for (int i=0; i<fields.length; i++) {
					Field field = fields[i];
					field.setAccessible(true);
					String fieldName = field.getName();
					
					MessageOptions options = field.getAnnotation(MessageOptions.class);
					boolean describeLastStrLen = options!=null?options.describeLastStrLen():false;
					boolean notNull = options!=null?options.notNull():true;
					if (options != null && options.ignore()) {
						continue;
					}
					
					try {
						Object fieldValue = field.get(targetObj);
						if (fieldValue == null && notNull) {
							throw new NullPointerException(field.getName() + " may not be null");
						}
						
						if (fieldValue == null && !notNull) {
							if (field.getType() == String.class) {
								fieldValue = " ";
							}
						}
						
						if (fieldValue instanceof Byte) {
							buf.writeByte((Byte)fieldValue);
						} else if (fieldValue instanceof Boolean) {
							buf.writeBoolean((Boolean)fieldValue);
						} else if (fieldValue instanceof Short) {
							buf.writeShort((Short)fieldValue);
						} else if (fieldValue instanceof Integer) {
							buf.writeInt((Integer)fieldValue);
						} else if (fieldValue instanceof Long) {
							buf.writeLong((Long)fieldValue);
						} else if (fieldValue instanceof Float) {
							buf.writeFloat((Float) fieldValue);
						} else if (fieldValue instanceof Double) {
							buf.writeDouble((Double) fieldValue);
						} else if (fieldValue instanceof String) {
							String strValue = (String) fieldValue;
							if (StringUtil.isEmpty(strValue)) {
								strValue = " ";
							}
							if (i == fields.length - 1 && !describeLastStrLen) {
								buf.writeBytes(strValue.getBytes(Charsets.UTF8));
							} else {
								byte[] strBytes = strValue.getBytes(Charsets.UTF8);
								buf.writeByte(strBytes.length);
								buf.writeBytes(strBytes);
							}
						} else {
							throw new IllegalArgumentException("could not convert " + fieldName + "[" + fieldValue.getClass().getName()+"] to bytes");
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				resultBytes = new byte[buf.readableBytes()];
				buf.readBytes(resultBytes);
			}
		}
		return resultBytes;
	}
	
	public static String formatToString(Object targetObj) {
		StringBuilder result = new StringBuilder();
		if (targetObj != null) {
			Field[] fields = targetObj.getClass().getDeclaredFields();
			if (fields != null) {
				Arrays.sort(fields, fieldComparator);
				for (Field field : fields) {
					field.setAccessible(true);
					String name = field.getName();
					Object value = null;
					try {
						value = field.get(targetObj);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					result.append(name + "=" + value);
					result.append(", ");
				}
			}
		}
		return result.toString();
	}
	
	public static class ReflectResult {
		public byte[] convertedBytes;
		
		public byte[] unconvertedBytes;
	}
}
