package jim.business.netsdk.ctrl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import jim.business.console.model.PassagewayData;
import jim.business.netsdk.model.FMSGCallBack;
import jim.business.netsdk.model.FMSGCallBack_V31;
import jim.business.netsdk.model.HCNetSDK;
import jim.business.netsdk.model.HCNetSDK.FRemoteConfigCallBack;

/** 
 * @ClassName: HCNetSDKCtrl 
 * @Description: 海康威视控制--用于连接海康威视硬件以及进行通信
 * @author DanielHyw
 * @date 2024年1月6日 下午5:09:33 
 *  
 */
public class HCNetSDKCtrl {
	private static final Logger log = LoggerFactory.getLogger(HCNetSDKCtrl.class);

	private Long deviceId;
	private int lUserID = -1;//用户句柄 实现对设备登录
	private int lAlarmHandle = -1;//报警布防句柄
	private int lAlarmHandle_V50 = -1; //v50报警布防句柄
	private int lListenHandle = -1;//报警监听句柄
	private HCNetSDK hCNetSDK = null;
	private FMSGCallBack_V31 fMSFCallBack_V31 = null;
	private FMSGCallBack fMSFCallBack=null;
	private HCNetSDK.NET_DVR_DEVICECFG m_strDeviceCfg;//设备信息
	
	private HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = null;//设备信息
	
	public boolean isLogin() {
		return lUserID != -1;
	}
	
	/**
	 * @Title: getSerialNumber 
	 * @Description: 获取设备序列号
	 * @return
	 *
	 */
	public String getSerialNumber() {
		return m_strDeviceCfg == null ? null : new String(m_strDeviceCfg.sSerialNumber).replaceAll("\\u0000", "");
	}

	public Long deviceId() {
		return deviceId;
	}

	public void init(Long deviceId, HCNetSDK hCNetSDK, FMSGCallBack_V31 fMSFCallBack_V31) {
		this.deviceId = deviceId;
		this.hCNetSDK = hCNetSDK;
		this.fMSFCallBack_V31 = fMSFCallBack_V31;
		
	}
	
	public boolean doLogin(String ip, short port, String username, String password) {
		log.info("开始登录超脑");
		if (isLogin()) {
			return true;
		}
		//登录设备
		if (!login_V40(ip, port, username, password)) {
			return false;
		}
        setAlarm();//报警布防，和报警监听二选一即可
        log.info("登录超脑完成");
        // 获取配置
        loadConfig();
        return true;
	}
	
	private String getChannelName(int lChannel) {
		IntByReference ibrBytesReturned = new IntByReference(0);//获取图片参数
		HCNetSDK.NET_DVR_PICCFG_V30 m_struPicCfg = new HCNetSDK.NET_DVR_PICCFG_V30();
        m_struPicCfg.write();
        Pointer lpPicConfig = m_struPicCfg.getPointer();
        boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_PICCFG_V30,
        		lChannel, lpPicConfig, m_struPicCfg.size(), ibrBytesReturned);
        if (!getDVRConfigSuc)
        	System.out.println("errorCode:" + hCNetSDK.NET_DVR_GetLastError() + " lChannel:" + lChannel);
        m_struPicCfg.read();
        String[] sName = new String[2];
        try {
			sName = new String(m_struPicCfg.sChanName, "GBK").split("\0", 2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return sName[0].replaceAll("\\u0000", "");
	}
	
	// 获取客流--未完成
	@Deprecated
	public void getRemoteConfig() {
		HCNetSDK.NET_DVR_PDC_QUERY_COND queryCond = new HCNetSDK.NET_DVR_PDC_QUERY_COND();
		queryCond.byReportType = 1;
		queryCond.dwChannel = 0;
		queryCond.struStartTime.wYear = 2023;
		queryCond.struStartTime.byMonth = 3;
		queryCond.struStartTime.byDay = 5;
		queryCond.struEndTime.wYear = 2024;
		queryCond.struEndTime.byMonth = 3;
		queryCond.struEndTime.byDay = 6;
		queryCond.byEnableProgramStatistics = 1;
		queryCond.byTriggerPeopleCountingData = 0;
		queryCond.dwPlayScheduleNo = 0;
		queryCond.write();
		Pointer lpPicConfig = queryCond.getPointer();
		HCNetSDK.NET_DVR_PDC_RESULT result = new HCNetSDK.NET_DVR_PDC_RESULT();
		Pointer resultConfig = result.getPointer();
		int isOk = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, 5089, lpPicConfig, queryCond.size(), new FRemoteConfigCallBack() {
			
			@Override
			public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
				// TODO Auto-generated method stub
				log.info("invoke");
			}
		} , resultConfig);

		if (isOk < 0) {
			System.out.println("errorCode:" + hCNetSDK.NET_DVR_GetLastError());
		}

		result.read();
	}
	
	private void loadConfig() {
		IntByReference ibrBytesReturned = new IntByReference(0);
        m_strDeviceCfg = new HCNetSDK.NET_DVR_DEVICECFG();
        m_strDeviceCfg.write();
        Pointer lpPicConfig = m_strDeviceCfg.getPointer();
        boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_DEVICECFG,
                0, lpPicConfig, m_strDeviceCfg.size(), ibrBytesReturned);
        m_strDeviceCfg.read();
        if (getDVRConfigSuc != true)
        {
            m_strDeviceCfg = null;
        }
	}
	
	/**
	 * 获取通道列表--只获取已连接的通道
	 * 
	 * @Title: getPassageList 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return
	 *
	 */
	public List<PassagewayData> getPassageList() {
		List<PassagewayData> list = new ArrayList<>();
		if (!isLogin()) {
			return list;
		}
		int maxIpChannelNum ;
		
        if (m_strDeviceInfo.struDeviceV30.byHighDChanNum == 0)
        {
            maxIpChannelNum = m_strDeviceInfo.struDeviceV30.byIPChanNum & 0xff;
            log.info("设备数组通道总数："+maxIpChannelNum);
        }else
        {
            maxIpChannelNum = (int)((m_strDeviceInfo.struDeviceV30.byHighDChanNum & 0xff) << 8);
            log.info("2设备数组通道总数："+maxIpChannelNum);
        }
        int group = maxIpChannelNum/64<=0?0:maxIpChannelNum/64-1;
        log.info(group + "");
        for (int i=0;i<=group;i++)
        {
//        	log.info("for group----------" + i + "------------");
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            boolean bRet;

            HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
            m_strIpparaCfg.write();
            //lpIpParaConfig 接收数据的缓冲指针
            Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
            bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, i, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
            m_strIpparaCfg.read();

            if (!bRet) {
//                //设备不支持,则表示没有IP通道
            } else {
                //设备支持IP通道
                for (int iChannum = 0; iChannum < ((maxIpChannelNum<=32)?32:64); iChannum++) {
                    m_strIpparaCfg.struStreamMode[iChannum].read();
                    if (m_strIpparaCfg.struStreamMode[iChannum].byGetStreamType == 0) {
                        m_strIpparaCfg.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
                        m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.read();

                        if (m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
                        	int id = iChannum + 64 * i + 1; // + m_strDeviceInfo.struDeviceV30.byStartDChan;
                        	PassagewayData data = new PassagewayData();

                        	int iChannelNumber = -1;
                        	int index = id - 1;
                            if((index < m_strDeviceInfo.struDeviceV30.byChanNum) && (index >=0))
                            {
                                iChannelNumber = index + m_strDeviceInfo.struDeviceV30.byStartChan;
                            }
                            else
                            {
                                iChannelNumber = 32 + (index - m_strDeviceInfo.struDeviceV30.byChanNum) + m_strDeviceInfo.struDeviceV30.byStartChan;
                            }

                            data.setName(getChannelName(iChannelNumber));
                            data.setIp(new String(m_strIpparaCfg.struIPDevInfo[iChannum].struIP.sIpV4).replaceAll("\\u0000", ""));
                            data.setPort(m_strIpparaCfg.struIPDevInfo[iChannum].wDVRPort);
                            data.setUsername(new String(m_strIpparaCfg.struIPDevInfo[iChannum].sUserName).replaceAll("\\u0000", ""));
                            data.setPassword(new String(m_strIpparaCfg.struIPDevInfo[iChannum].sPassword).replaceAll("\\u0000", ""));
                        	data.setId(id);
                        	data.setEnabled(true);
                        	list.add(data);
                        }
                        else {

                        }
                    }
                }
            }
        }
		
		return list;
	}
	
	private boolean login_V40(String ip, short port, String user, String psw) {
        //注册
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        String m_sDeviceIP = ip;//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        String m_sUsername = user;//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        String m_sPassword = psw;//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = port;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
//        m_strLoginInfo.byLoginMode=1;  //ISAPI登录
        m_strLoginInfo.write();

        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            return false;
        } else {
            return true;
        }
    }
	
	private void login_V30(String ip, short port, String user, String psw) {
        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        lUserID = hCNetSDK.NET_DVR_Login_V30(ip, port, user, psw, m_strDeviceInfo);

        if ((lUserID == -1) || (lUserID == 0xFFFFFFFF)) {
            return;
        } else {
            return;
        }
    }
	
	private void setAlarm() {
        if (lAlarmHandle < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 0;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);

            if (lAlarmHandle == -1) {
                return;
            } else {
            }
        } else {
        }
    }
	
	private void setAlarm_V50() {

        if (lAlarmHandle_V50 < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50 m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 1;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 1;   //布防类型 0：客户端布防 1：实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V50(lUserID, m_strAlarmInfo, Pointer.NULL, 0);

            if (lAlarmHandle == -1) {
                return;
            } else {
            }
        } else {
        }

    }
	
	private void startListen(String ip, short port) {
        if (fMSFCallBack == null) {
            fMSFCallBack = new FMSGCallBack();
        }
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(ip, port, fMSFCallBack_V31, null);
        if (lListenHandle == -1) {
            return;
        } else {
        }
    }
	
	public void doLogout() {

        if (lAlarmHandle > -1) {
            if (!hCNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle)) {
            	lAlarmHandle = -1;
            }
        }
        if (lListenHandle > -1) {
            if (!hCNetSDK.NET_DVR_StopListen_V30(lListenHandle)) {
            	lListenHandle = -1;
            }
        }
        if (lUserID > -1) {
        	if (hCNetSDK.NET_DVR_Logout(lUserID)) {
        		lUserID = -1;
            }
        }
    }
}
