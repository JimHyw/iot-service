package jim.framework.system.manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jim.framework.system.model.BaseReturn;

/** 
* @ClassName: UpdateManager 
* @Description: 推送消息管理
* @author DanielHyw
* @date Aug 3, 2021 4:22:40 PM 
*  
*/
@Component
public class UpdateManager implements CommandLineRunner {

	private static UpdateManager _instance;
	
	public static UpdateManager getInstance() {
		return _instance;
	}
	
	@Override
	public void run(String... args) throws Exception {
		_instance = this;
	}
	
	
	/** 
	* @Title: makeUpdateData 
	* @Description: 组合推送消息
	* @param br  参数说明 
	* @return void    返回类型 
	* 
	*/
	public void makeUpdateData(BaseReturn br) {
		Map<String, Object> update = br.getUpdate() == null ? new HashMap<String, Object>() : br.getUpdate();
//		String userId = ShiroUtil.getUserId();
//		if(userId != null) {
//			EUserType userType = ShiroUtil.getUserType();
//			if(userType != EUserType.manager) {
//			}
//		}
		br.setUpdate(update);
	}
}
