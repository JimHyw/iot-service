package jim.framework.util;

import java.util.regex.Pattern;
/**
 * 输入参数验证工具类
 *
 */
public class ValidatorCheckUtil {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,30}$";
 
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
 
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
 
    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    
    
    /**
     * 正则表达式：验证护照
     */
    public static final String REGEX_ID_PASSPORT = "^[a-zA-Z0-9]{3,21}$";
    
    
    /**
     * 正则表达式：验证军官证
     */
    public static final String REGEX_COO = "^[a-zA-Z0-9]{7,21}$";
 
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }
 
    /**
     * 校验手机号
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
 
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
    
    /**
     * 校验护照
     * 
     * @param passport
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassport(String passport) {
        return Pattern.matches(REGEX_ID_PASSPORT, passport);
    }
    
    /**
     * 校验军官证
     * 
     * @param coo
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isCoo(String coo) {
        return Pattern.matches(REGEX_COO, coo);
    }
}