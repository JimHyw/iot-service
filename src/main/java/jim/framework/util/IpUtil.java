package jim.framework.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址
 */
public class IpUtil {

	private static Logger LOG = LoggerFactory.getLogger(IpUtil.class);

	/**
	 * 获取IP地址
	 *
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null, unknown = "unknown", seperator = ",";
		int maxLength = 15;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			LOG.error("IpUtils ERROR ", e);
		}

		// 使用代理，则获取第一个IP地址
		if (StringUtils.isEmpty(ip) && ip.length() > maxLength) {
			int idx = ip.indexOf(seperator);
			if (idx > 0) {
				ip = ip.substring(0, idx);
			}
		}

		return ip;
	}

	/**
	 * 获取ip地址
	 * @return
	 */
	public static String getIpAddr() {
		HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
		return getIpAddr(request);
	}

}
