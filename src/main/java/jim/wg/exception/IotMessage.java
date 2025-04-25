package jim.wg.exception;

public enum IotMessage {
    succe("0000", "成功！"),
    dbbug("8888", "数据库异常！"),
    fail("9999", "失败！");


    private String code;
    private String message;

    private IotMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
