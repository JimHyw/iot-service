package jim.framework.websocket.enums;

/**
 * @author DanielHyw
 * @ClassName: EWebSocketCmd
 * @Description: websocket指令枚举
 * @date Jan 11, 2021 5:46:27 PM
 */
public enum EWebSocketCmd {
    /**
     * 通用消息
     */
    Common,
    //-----------本地服务推送消息----------
    /**
     * 心跳检测
     */
    Heartbeat,
    /**
     * 更新通道信息
     */
    UpdatePassageway,
    /**
     * 开始预警
     */
    StartWarning,
    /**
     * 结束预警
     */
    StopWarning,
    /**
     * 更新设备状态
     */
    UpdateDeviceStatus,
    /**
     * 更新通道图片
     */
    UpdatePassagewayImage,
    /**
     * 更新通道图片--新模式，单张图片同步
     */
    UpdatePassagewayImageV2,
    /**
     * 更新预警相机图片
     */
    UpdateWarningCameraImage,

    //-----------以下中心请求-----------
    /**
     * 初始化设备
     */
    InitDevice,
    /**
     * 添加设备
     */
    AddDevice,
    /**
     * 更新设备
     */
    UpdateDevice,
    /**
     * 删除设备
     */
    DelDevice,
    /**
     * 获取设备列表
     */
    ListDevice,
    /**
     * 获取通道列表
     */
    ListPassageway,
    /**
     * 开始推流
     */
    StartPushFlow,
    /**
     * 结束推流
     */
    StopPushFlow,
    /**
     * 结束所有推流
     */
    StopAllPushFlow,
    /**
     * 开始回放推流
     */
    StartPlayback,
    /**
     * 开始预警推流
     */
    StartWarningPushFlow,
    /**
     * 开始回放预警推流
     */
    StartWarningPlayback,
    /**
     * 开始推流--根据推流地址和拉流地址直接推流
     */
    StartPushFlowEx,
    /**
     * 相机抓图--目前可能会用在道闸抓图，暂时未实现逻辑
     */
    GetScreenshot,
    //-----------以下预警信息推送到中心服务器-----------
    /**
     * 更新预警设备
     */
    UpdateWarningDevice,
    /**
     * 更新预警摄像头
     */
    UpdateWarningCameras,
    /**
     * 推送相机预警数据
     */
    PushWarningCameraEvent,
    /**
     * 推送摄像机状态预警
     */
    PushWarningCameraStatusEvent,
    /**
     * 获取微耕通行记录
     */
    WgTransitRecord,
	//-----------以下人流相关-----------
	//-----------以下人流相关中心请求-----------
    /**
     * 刷新人流设备列表
     */
	RefreshTrafficDevice,
	/**
	 * 添加人流设备
	 */
	AddTrafficDevice,
	/**
	 * 删除人流设备
	 */
	DelTrafficeDevice,
	//-----------以下人流相关推送到中心服务器-----------
	/**
	 * 推送人流信息
	 */
	PushTrafficInfo,
}
