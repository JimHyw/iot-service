package jim.wg.exception;


// 状态码和状态描述
public enum ResponseEnum {

    SUCCESS(200, "成功"), FAIL_LOCK(204, "有人在操作"), FAIL_TEMPLATE(205, "模板有误"), FAIL(500, "失败"), FAIL_TOKEN_NO(100, "验证失败！无token！"), FAIL_TOKEN_ERROR(101, "token无效！"), FAIL_TOKEN_USER_ERROR(102, "token用户不存在！"), FAIL_TOKEN_SECRET_ERROR(103, "用户token不存在！"), FAIL_NOT_LOGGIN(400, "对不起,您还未登录,请重新登录!"), FAIL_NOT_PERMISSIONS(403, "对不起,您暂时没有访问权限,请联系管理员开通相应权限!"), FAIL_NOT_FIND(404, "找不到"), FAIL_ERROR_PARAMETER(405, "参数错误"), FAIL_ERROR_BIZ(407, "此请求不能重复调用");

    // 响应状态码
    private int code;

    // 响应消息
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
