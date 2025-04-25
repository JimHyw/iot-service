package jim.wg.exception;

/**
 * 自定义业务异常
 *
 * @author dongjian 384880231@qq.com
 * @since 3.0 2022-12-16
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorInfo errorInfo;

    public BizException(String message) {
        super(message);
        this.errorInfo = new ErrorInfo(ResponseEnum.FAIL.getCode(), message);
    }

    public BizException(ResponseEnum re) {
        super(re.getMsg());
        this.errorInfo = new ErrorInfo(re.getCode(), re.getMsg());
    }

    @Override
    public String getMessage() {
        if (errorInfo != null) {
            return errorInfo.getErrorMessage();
        }
        return super.getMessage();
    }

    public BizException(ErrorInfo errorInfo) {
        super();
        this.errorInfo = errorInfo;
    }

    public BizException(String code, String message) {
        super();
        this.errorInfo = new ErrorInfo(code, message);
    }

    public BizException(IotMessage m) {
        super();
        this.errorInfo = new ErrorInfo(m);
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }
}
