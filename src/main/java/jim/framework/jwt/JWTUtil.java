package jim.framework.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jim.framework.system.model.EUserType;
import jim.framework.util.DateUtil;
import jim.framework.util.MD5Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JWT token生成，校验工具类
 */
@ConfigurationProperties(prefix = "jwt")
@Component
public class JWTUtil {
	
	/**
     * logger
     */
	Logger log = Logger.getLogger(JWTFilter.class.getName());

    /**
     * 密钥
     */
    private static final String secret = "@$20402zlfz^*";
    
    /**
     * 有效期限 分钟
     */
    private static final int expire = 600;
    
    /**
     * 存储 token
     */
    private static Map<String, Object> header = new HashMap<String, Object>();
    
    static{
    	header.put("typ", "JWT");
    	header.put("alg", "HS512");
    }

    /**
     * 生成jwt token
     *
     * @param userId 用户ID
     * @return token
     */
    public static String generateToken(String userId) {
        Date nowDate = new Date();

        return Jwts.builder()
                .setHeaderParams(header)
                // 后续获取 subject 是 userid
                .setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(DateUtil.addMinutes(nowDate, expire))
                // 这里我采用的是 HS512 算法
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /** 
    * @Title: generateTokenEx 
    * @Description: 生成jwt token
    * @param userId
    * @return  参数说明 
    * @return JWTData    返回类型 
    * 
    */
    public static JWTData generateTokenEx(String userId, EUserType type, int loginCount, int sourceType) {
        Date nowDate = new Date();
        String nowSecret = MD5Util.encrypt(secret + userId + System.currentTimeMillis());
        // 后台token12个小时，微信端365天
        Date expiration = sourceType == 1 ? DateUtil.addMinutes(nowDate, expire) : DateUtil.addDays(nowDate, 365);
        String token = Jwts.builder()
                .setHeaderParams(header)
                // 后续获取 subject 是 userid,type, oginCount,sourceType
                .setSubject(userId + "," + type.toString() + "," + loginCount + "," + sourceType)
                .setIssuedAt(nowDate)
                .setExpiration(expiration)
                // 这里我采用的是 HS512 算法
                .signWith(SignatureAlgorithm.HS512, nowSecret)
                .compact();
        return new JWTData(nowSecret, token);
        
    }

    /**
     * * 解析 token
     * @param token
     * @return 解析结果
     */
    public static Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
    
    public static String getSecret(String userId){
    	return secret;
    }
}
