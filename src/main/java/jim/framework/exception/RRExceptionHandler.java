package jim.framework.exception;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jim.business.exception.BusinessException;
import jim.framework.core.exception.HttpException;
import jim.framework.system.model.BaseReturn;

/**
 * 自定义异常处理器
 */
@RestControllerAdvice
public class RRExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public BaseReturn handleRRException(RRException e){
		logger.error(e.getMessage(), e);
		return BaseReturn.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public BaseReturn handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return BaseReturn.error(20001, "数据库中已存在该记录: msg:" + e.getCause().getMessage());
	}

	@ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
	public BaseReturn handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage(), e);
		return BaseReturn.error("没有权限，请联系管理员授权");
	}
	
	/** 
	* @Title: handleBusinessException 
	* @Description: 业务异常
	* @param e
	* @return  参数说明 
	* @return BaseReturn    返回类型 
	* 
	*/
	@ExceptionHandler(BusinessException.class)
	public BaseReturn handleBusinessException(BusinessException e) {
		logger.error(e.toString(), e);
		return BaseReturn.error(e.getErrCode(), e.getMessage());
	}
	
	@ExceptionHandler(HttpException.class)
	public BaseReturn handleBusinessException(HttpException e) {
		logger.error(e.toString(), e);
		return BaseReturn.error(e.getErrCode(), e.getErrorMsg());
	}

	@ExceptionHandler(Exception.class)
	public BaseReturn handleException(Exception e){
		logger.error(e.getMessage(), e);
		return BaseReturn.error();
	}
	
}
