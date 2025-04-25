package jim.wg.exception;

public class ErrorInfo {

    /**
     * 错误码
     **/
    private String errorCode;
    /**
     * 错误信息
     **/
    private String errorMessage;

    public ErrorInfo() {
    }

    public ErrorInfo(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorInfo(int errorCode, String errorMessage) {
        this.errorCode = errorCode + "";
        this.errorMessage = errorMessage;
    }

    public ErrorInfo(IotMessage m) {
        this.errorCode = m.getCode();
        this.errorMessage = m.getMessage();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return this.errorCode + ":" + this.errorMessage;
    }
}
