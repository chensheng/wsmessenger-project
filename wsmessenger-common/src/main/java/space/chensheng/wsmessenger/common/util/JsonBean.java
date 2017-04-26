package space.chensheng.wsmessenger.common.util;

public abstract class JsonBean {
	
	@Override
	public String toString() {
		return JsonMapper.nonEmptyMapper().toJson(this);
	}
}
