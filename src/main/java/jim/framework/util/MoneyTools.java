package jim.framework.util;

/** 
* @ClassName: MoneyTools 
* @Description: 金钱工具
* @author DanielHyw
* @date May 8, 2020 6:25:41 PM 
*  
*/
public class MoneyTools {

	/** 
	* @Title: toMoneyString 
	* @Description: 分转换元
	* @param cent
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public static String toMoneyString(String cent)
	{
		if(cent.length() < 3)
		{
			// 小于一元
			if(cent.length() == 1)
				return "0.0" + cent;
			return "0." + cent;
		}
		int cut = cent.length() - 2;
		return cent.substring(0, cut) + "." + cent.substring(cut);
	}
	
	/** 
	* @Title: toMoneyString 
	* @Description: 分转换元
	* @param cent
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public static String toMoneyString(long cent)
	{
		return toMoneyString(String.valueOf(cent));
	}
}
