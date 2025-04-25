package jim.wg.utils;

/**
 * 微耕API路径
 */
public interface ConstWg {

    public final static String HOST_URL = "http://localhost:61080/";//服务器地址
    public final static Long TEST_CTL_SN = 225079399l; //控制器编号
    public final static Long CARD_NO = 18349461l; //卡号
    public final static int DOOR_NO = 2; //门号
    public final String doorMode1 = "在线";
    public final String doorMode2 = "常开";
    public final String doorMode3 = "常闭";
    public final static int door_delay1 = 3;  //默认3秒, 可设置25秒
    public final static int door_delay2 = 25; //25秒

    public final static String REMOTE_OPEN_DOOR = "远程开门";//远程开门
    public final static String SELECT_CTL_STATUS = "查询控制器状态";//查询控制器状态
    public final static String SET_TIME = "设置日期时间";//校准时间
    public final static String PICK_UP_RECORD = "提取记录";//提取记录
    public final static String CONFORM_RECEIVED_RECORD = "确认已接收记录";//恢复已提取记录
    public final static String PICK_UP_RECORD1 = "提取记录";//提取记录_指定时间范围_当天记录
    public final static String SET_DOOR_CTR_PARAM = "设置门控制参数";//设置门控制参数在线
    public final static String SET_DOOR_CTR_PARAM1 = "设置门控制参数";//强制门常开
    public final static String GET_DOOR_CTR_PARAM = "读取门控制参数";//读取门控制参数
    public final static String UPLOAD_ALL_RIGHTS = "上传全部权限";//上传全部权限1万人
    public final static String RIGHT_TOTAL_GET = "权限总数读取";//权限总数读取
    public final static String RIGHT_ADD_EDIT = "权限添加或修改";//单个权限添加修改
    public final static String RIGHT_DELETE = "权限删除";//权限删除
    public final static String RIGHT_SELECT = "权限查询";//权限查询
    public final static String RIGHT_TOTAL_GET1 = "权限总数读取";//提取权限
    public final static String UPLOAD_ALL_USEABLE_TIME_RANGE = "上传全部有用时段";//上传全部有用时段测试10个
    public final static String PICK_UP_TIME_RANGE = "提取时段";//提取时段
    public final static String UPLOAD_ALL_HOLIDAY_CHECK = "上传全部假期约束";//上传全部假期约束
    public final static String FUNCTION_SET = "功能设置";//二维码_启用二维码透传
    public final static String CREATE_QR_INFO = "生成二维码信息";//二维码_获取二维码信息12345678901
    public final static String REMOTE_OPEN_DOOR1 = "远程开门";//梯控_1b远程开门_直达楼层号
    public final static String UPLOAD_ALL_RIGHTS1 = "上传全部权限";//梯控_5a上传全部权限1万人
    public final static String RIGHT_ADD_EDIT1 = "权限添加或修改";//梯控_5c单个权限添加修改
    public final static String FUNCTION_SET1 = "功能设置";//梯控_5d允许常开楼层
    public final static String FUNCTION_SET2 = "功能设置";//启用输入6位临时密码作卡号
    public final static String RIGHT_ADD_EDIT2 = "权限添加或修改";//给6位临时密码授权_当天一次有效
    public final static String COM_TRANS = "串口透传";//串口透传

}
