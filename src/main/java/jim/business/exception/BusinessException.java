package jim.business.exception;


/** 
* @ClassName: BusinessException 
* @Description: 业务异常
* @author DanielHyw
* @date Apr 10, 2020 2:56:14 PM 
*  
*/
public class BusinessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3647148792165352312L;

	private String message;
	
	private int errCode = 10001;
	
	public BusinessException(String message) {
		super(message);
		this.message = message;
	}
	
	public BusinessException(int errCode, String message) {
		super(message);
		this.errCode = errCode;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getErrCode() {
		return errCode;
	}

	@Override
	public String toString() {
		return this.errCode + ":" + message;
	}
}
