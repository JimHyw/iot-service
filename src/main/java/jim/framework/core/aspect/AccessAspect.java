package jim.framework.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jim.framework.constant.SystemConstant;
import jim.framework.core.annotation.Access;
import jim.framework.core.exception.HttpException;
import jim.framework.system.model.EUserType;
import jim.framework.util.ShiroUtil;

/**
 * AOP实现类
 */
@Aspect
@Component
public class AccessAspect {
	
	/**
	 * 切点
	 */
	@Pointcut("@annotation(jim.framework.core.annotation.Access) || @within(jim.framework.core.annotation.Access)")
	public void accessPointCut() { 
		
	}
	
	/**
	 * 方法前执行
	 * @param joinPoint
	 */
	@Before("accessPointCut()")
	public void before(JoinPoint joinPoint){
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		// Method method = signature.getMethod();
		String methodName = signature.getName(); //signature.getName().toLowerCase();
		Access access = (Access) signature.getMethod().getDeclaredAnnotation(Access.class);
		if(access.userType().length > 0) {
			boolean isAllowed = false;
			EUserType doUserType = ShiroUtil.getUserType();
			for(EUserType userType : access.userType()) {
				if(doUserType == userType) {
					isAllowed = true;
					break;
				}
			}
			if(!isAllowed) {
				throw new HttpException(SystemConstant.UNAUTHORIZED, "权限不足");
			}
		}
	}
}
