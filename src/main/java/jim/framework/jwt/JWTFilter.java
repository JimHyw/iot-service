package jim.framework.jwt;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jim.framework.util.ShiroUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;



/**
 * JWT权限过滤器
 *																																																																																																																	
 */
@Component
public class JWTFilter extends FormAuthenticationFilter {


	@Value("${system.code}")
	private String systemCode;
//	
//	/** 
//	* @Fields tokenService : token服务
//	*/ 
//	@Autowired
//	private SystemTokenService tokenService;
//	
//	/** 
//	* @Fields authFunService : 方法权限服务
//	*/ 
//	@Autowired
//	private AuthFunctionService authFunService;
	
	
	/**
	 * logger
	 */
	Logger log = Logger.getLogger(JWTFilter.class.getName());
	
	

	/**
	 * shiro权限拦截核心方法 返回true允许访问resource，
	 *
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return 返回 false 将进入onAccessDenied
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue)  {
		String token = getRequestToken((HttpServletRequest) request);
		// ShiroUtil.setUserId(null);
        ShiroUtil.set(null);
		if(token == null || token.equals("")){
			return true;
		}
		return true;
//		CheckResult result = null;
//		try{
//			//JWTVerifier jwtUtil = JWTVerifier.create(systemCode, redisService.getRedisTemplate());
//			JWTVerifier jwtUtil = JWTVerifier.create(systemCode, tokenService, authFunService, adminUserService);
//			result = jwtUtil.checkToken(token);
//		}
//		catch(Exception ex){
//			throw new HttpException(SystemConstant.NOT_LOGIN, "未登录");
//		}
//		if(result.getCode().equals("100")){
//			// ShiroUtil.setUserId(result.getUserId());
//			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
//			map.put("userId",result.getUserId());
//			map.put("userType", result.getUserType());
//			map.put("userIp",IpUtil.getIpAddr((HttpServletRequest)request));
//			map.put("userSourceType",result.getUserSourceType());
//            ShiroUtil.set(map);
//			return true;
//		}
//		else if(result.getCode().equals("101") || result.getCode().equals("103")){
//			throw new HttpException(SystemConstant.TOKEN_EXPRIED, "登录超时");
//		}
//		else if(result.getCode().equals("104")){
//			throw new HttpException(SystemConstant.TOKEN_OTHERLOGIN, "其他地区已登录");
//		}
//		else if(result.getCode().equals("105")){
//			throw new HttpException(SystemConstant.TOKEN_OUT, "您已被t下线");
//		}
//		else{
//			throw new HttpException(SystemConstant.TOKEN_SIGNATURE, "非法token");
//		}
	}

	/**
	 * 当访问拒绝时是否已经处理了； 如果返回true表示需要继续处理； 如果返回false表示该拦截器实例已经处理完成了，将直接返回即可。
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {

		return false;
	}
	
	/**
	 * 获取请求的token
	 */
	private String getRequestToken(HttpServletRequest httpRequest) {
		// 从header中获取token
		String token = httpRequest.getHeader("authorization");
		
		// 如果header中不存在token，则从参数中获取token
//		if (StringUtils.isBlank(token)) {
//			return httpRequest.getParameter("authorization");
//		}
//		if (StringUtils.isBlank(token)) {
//
//			// 从 cookie 获取 token
//			Cookie[] cookies = httpRequest.getCookies();
//			if (null == cookies || cookies.length == 0) {
//				return null;
//			}
//			for (Cookie cookie : cookies) {
//				if (cookie.getName().equals("authorization")) {
//					token = cookie.getValue();
//					break;
//				}
//			}
//		}
		return token;
	}
}