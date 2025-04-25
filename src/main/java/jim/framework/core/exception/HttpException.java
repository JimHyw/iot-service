package jim.framework.core.exception;


/** 
* @ClassName: HttpException 
* @Description: http异常
* @author DanielHyw
* @date Apr 15, 2020 10:55:03 AM 
*  
*/
public class HttpException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	private String errorMsg;
	
	private int errorCode;

	public HttpException(int code) {
		this.errorCode = code;
	}
	
	public HttpException(int code, String msg) {
		this.errorCode = code;
		this.errorMsg = msg;
	}

	@Override
	public String getMessage() {
		return String.valueOf(errorCode);
	}
	
	public int getErrCode() {
		return errorCode;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
