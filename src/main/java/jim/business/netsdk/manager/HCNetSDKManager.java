package jim.business.netsdk.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import jim.business.console.model.DeviceData;
import jim.business.console.model.PassagewayData;
import jim.business.console.model.PassagewayList;
import jim.business.netsdk.ctrl.HCNetSDKCtrl;
import jim.business.netsdk.model.FMSGCallBack_V31;
import jim.business.netsdk.model.HCNetSDK;
import jim.business.netsdk.util.osSelect;
import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.manager.WebSocketManager;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HCNetSDKManager implements CommandLineRunner{
	@Autowired
	private WebSocketManager webSocketManager;
	
	/**
	 * 单例，用于非springboot架构的内容访问此单例
	 */
	public static HCNetSDKManager impl;
	
	private HCNetSDK hCNetSDK = null;
	
	private FMSGCallBack_V31 fMSFCallBack_V31 = null;

	//Login_V40(0, "192.168.2.141", (short) 8000, "admin", "zwbsc235");
	
	private Map<Long, HCNetSDKCtrl> ctrlMap = new ConcurrentHashMap<>();
	
	private Map<Long, List<PassagewayData>> passagewayListMap = new ConcurrentHashMap<>();
	
	public HCNetSDKManager() {
		impl = this;
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		log.info("start HCNetSDKManager");
		// 初始化sdk
		if (hCNetSDK == null) {
            if (!CreateSDKInstance()) {
            	log.error("Load SDK fail");
                return;
            }
        }
        //linux系统建议调用以下接口加载组件库
        if (osSelect.isLinux()) {
            HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
            HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
            //这里是库的绝对路径，请根据实际情况修改，注意改路径必须有访问权限
            String strPath1 = "/home/LinuxSDK/libcrypto.so.1.1";
            String strPath2 = "/home/LinuxSDK/libssl.so.1.1";

            System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
            ptrByteArray1.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());

            System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
            ptrByteArray2.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());

            String strPathCom = "/home/LinuxSDK/";
            HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
            System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
            struComPath.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
        }

        /**初始化*/
        hCNetSDK.NET_DVR_Init();
        /**加载日志*/
        hCNetSDK.NET_DVR_SetLogToFile(3, "./sdklog", false);
        //设置报警回调函数
        if (fMSFCallBack_V31 == null) {
            fMSFCallBack_V31 = new FMSGCallBack_V31();
            Pointer pUser = null;
            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
            	log.error("设置回调函数失败!");
                return;
            } else {
            	log.info("设置回调函数成功!");
            }
        }
        /** 设备上传的报警信息是COMM_VCA_ALARM(0x4993)类型，
         在SDK初始化之后增加调用NET_DVR_SetSDKLocalCfg(enumType为NET_DVR_LOCAL_CFG_TYPE_GENERAL)设置通用参数NET_DVR_LOCAL_GENERAL_CFG的byAlarmJsonPictureSeparate为1，
         将Json数据和图片数据分离上传，这样设置之后，报警布防回调函数里面接收到的报警信息类型为COMM_ISAPI_ALARM(0x6009)，
         报警信息结构体为NET_DVR_ALARM_ISAPI_INFO（与设备无关，SDK封装的数据结构），更便于解析。*/
        HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struNET_DVR_LOCAL_GENERAL_CFG = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
        struNET_DVR_LOCAL_GENERAL_CFG.byAlarmJsonPictureSeparate = 0;   //设置JSON透传报警数据和图片分离
        struNET_DVR_LOCAL_GENERAL_CFG.write();
        Pointer pStrNET_DVR_LOCAL_GENERAL_CFG = struNET_DVR_LOCAL_GENERAL_CFG.getPointer();
        hCNetSDK.NET_DVR_SetSDKLocalCfg(17, pStrNET_DVR_LOCAL_GENERAL_CFG);
        log.info("初始化sdk完成!");
        // 定时刷新通道列表
        new Thread(() -> {
        	while (true) {
        		try {
        			Thread.sleep(30000);
    				log.debug("开始刷新通道");
    				refreshPassageway();
    				// 每300秒检测一次
    				Thread.sleep(300000);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
			}
        }).start();
		
	}
	
	/**
	 * 创建设备控制
	 * 
	 * @Title: createCtrl 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param deviceData
	 *
	 */
	public void createCtrl(DeviceData deviceData) {
		// 先判断是否存在
		HCNetSDKCtrl ctrl = ctrlMap.get(deviceData.getId());
		if (ctrl == null) {
			// 不存在，则新增
			ctrl = new HCNetSDKCtrl();
			ctrl.init(deviceData.getId(), hCNetSDK, fMSFCallBack_V31);
			ctrlMap.put(deviceData.getId(), ctrl);
		} else {
			passagewayListMap.remove(deviceData.getId());
			ctrl.doLogout();
		}
		boolean loginOk = false;
		try {
			loginOk = ctrl.doLogin(deviceData.getIp(), deviceData.getPort(), deviceData.getUsername(), deviceData.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
		deviceData.setEnabled(loginOk);
		deviceData.setSerialNumber(ctrl.getSerialNumber());
	}
	
	/**
	 * 创建其他设备控制--用于非标准管理的设备
	 * 
	 * @Title: createOtherCtrl 
	 * @Description:
	 * @param id
	 * @return
	 *
	 */
	public HCNetSDKCtrl createOtherCtrl(long id) {
		HCNetSDKCtrl ctrl = new HCNetSDKCtrl();
		ctrl.init(id, hCNetSDK, fMSFCallBack_V31);
		return ctrl;
	}
	
	public void removeCtrl(long id) {
		HCNetSDKCtrl ctrl = ctrlMap.remove(id);
		if (ctrl != null) {
			ctrl.doLogout();
			ctrl = null;
		}
		passagewayListMap.remove(id);
	}
	
	public List<PassagewayData> listPassagewayByDeviceId(Long deviceId) {
		HCNetSDKCtrl ctrl = ctrlMap.get(deviceId);
		if (ctrl == null) {
			return null;
		}
		List<PassagewayData> list = passagewayListMap.get(deviceId);
		if (list == null) {
			list = ctrl.getPassageList();
			passagewayListMap.put(deviceId, list);
		}
		return list;
	}
	
	/**
	 * 
	 * 
	 * @Title: refreshPassageway 
	 * @Description: 刷新通道信息，有变化则同步给中心服务器
	 *
	 */
	private void refreshPassageway() {
		if (ctrlMap.isEmpty()) {
			return;
		}
		
		// 刷新通道
		List<Long> deviceIdList = new ArrayList<>(ctrlMap.keySet());
		
		for (Long deviceId : deviceIdList) {
			HCNetSDKCtrl ctrl = ctrlMap.get(deviceId);
			if (ctrl == null)
				return;
			List<PassagewayData> list = ctrl.getPassageList();
			// 不在判断是否有变化，直接同步全部。方便ip，账号等信息变更后的同步
			log.debug("发送通道列表：" + list.size());
			webSocketManager.send(EWebSocketCmd.UpdatePassageway, new PassagewayList(deviceId, list));
		}
	}
	
	private boolean CreateSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    if (osSelect.isWindows())
                        //win系统加载库路径
                    	// strDllPath = "HCNetSDK";
                        strDllPath = System.getProperty("user.dir") + "\\HCNetSDK\\HCNetSDK.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strDllPath = "/home/LinuxSDK/libhcnetsdk.so";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return true;
    }
}
