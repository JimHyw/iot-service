package jim.business.netsdk.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import jim.business.netsdk.ctrl.PushFlowCtrl;
import jim.framework.common.TimerExpireHashMap;

/** 
 * @ClassName: PushFlowManager 
 * @Description: 推流管理
 * @author DanielHyw
 * @date 2024年1月10日 上午9:40:44 
 *  
 */
@Component
public class PushFlowManager {
	/**
	 * 推流过期时间--本地使用，一般情况用不到，遇到掉包时的保护措施
	 * 当前设置2分钟
	 */
	private final long playExpireTime = 2 * 60;
	
	/** 
	 * 推流控制字典，按推流地址保存
	 */ 
	private TimerExpireHashMap<String, PushFlowCtrl> pushFlowCtrlMap = new TimerExpireHashMap<>();
	
	private TimerExpireHashMap.TimerExpireHashMapCallback<String, PushFlowCtrl> timerExpireHashMapCallback = new TimerExpireHashMap.TimerExpireHashMapCallback<String, PushFlowCtrl>() {

		@Override
		public void callback(String key, PushFlowCtrl ctrl) throws RuntimeException {
			ctrl.stop();
		}
	};
	
	public TimerExpireHashMap<String, PushFlowCtrl> getPushFlowCtrlMap() {
		return pushFlowCtrlMap;
	}
	
	public PushFlowManager() {
		pushFlowCtrlMap.setTimerExpireHashMapCallback(timerExpireHashMapCallback);
	}
	
	public void startPush(String playKey, String pullUrl, String pushUrl) {
		PushFlowCtrl ctrl = pushFlowCtrlMap.get(playKey);
		if (ctrl == null) {
			ctrl = new PushFlowCtrl();
			ctrl.init(pullUrl, pushUrl);
			pushFlowCtrlMap.put(playKey, ctrl, playExpireTime);
		} else if (ctrl.isRunning()) {
			// 已经在推流
			// 判断拉流地址是否是同一个，不是则先停止老的，然后发起新的拉流
			if (pullUrl.equals(ctrl.getPullUrl())) {
				return;
			}
			pushFlowCtrlMap.setKeyExpireTime(playKey, playExpireTime);
			ctrl.init(pullUrl, pushUrl);
		}
		ctrl.start();
		
	}
	
	public void stopPush(String playKey) {
		PushFlowCtrl ctrl = pushFlowCtrlMap.remove(playKey);
		if (ctrl == null) {
			return;
		}
		ctrl.stop();
		
	}
	
	public void stopAllPush() {
		if (pushFlowCtrlMap.isEmpty()) {
			return;
		}
		for (PushFlowCtrl ctrl : pushFlowCtrlMap.values()) {
			ctrl.stop();
		}
		pushFlowCtrlMap.clear();
	}

	public void refreshPlayExpireTime(List<String> playKeys) {
		if (!playKeys.isEmpty()) {
			for (String playKey : playKeys) {
				pushFlowCtrlMap.setKeyExpireTime(playKey, playExpireTime);
			}
		}
	}
}
