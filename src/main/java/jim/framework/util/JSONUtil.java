package jim.framework.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * json工具类(jackjson)
 */
public class JSONUtil {

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private JSONUtil() {

	}

	public static ObjectMapper getInstance() {
		return objectMapper;
	}

	/**
	 * object转化json
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String beanToJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * json转对象
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T jsonToBean(String jsonStr, Class<T> clazz) {
		try {
			return objectMapper.readValue(jsonStr, clazz);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * json转map
	 * 
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, Object> jsonToMap(String jsonStr)
			throws Exception {
		return objectMapper.readValue(jsonStr, Map.class);
	}

	/**
	 * json转map，指定类型
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> jsonToMap(String jsonStr, Class<T> clazz)
			throws Exception {
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) objectMapper.readValue(jsonStr,
				new TypeReference<Map<String, T>>() {
				});
		Map<String, T> result = new HashMap<String, T>();
		for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
			result.put(entry.getKey(), mapToBean(entry.getValue(), clazz));
		}
		return result;
	}
	
	public static Map<String, List<String>> jsonToMapListString(String jsonStr)
			throws Exception {
		Map<String, List<String>> map = objectMapper.readValue(jsonStr,
				new TypeReference<Map<String, List<String>>>() {
				});
		
		return map;
	}

	/**
	 * json数组转list，指定类型
	 * 
	 * @param jsonArrayStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> jsonToListObject(String jsonArrayStr, Class<T> clazz)
			throws Exception {
		List<Map<String, Object>> list = (List<Map<String, Object>>) objectMapper.readValue(jsonArrayStr,
				new TypeReference<List<T>>() {
				});
		List<T> result = new ArrayList<T>();
		for (Map<String, Object> map : list) {
			result.add(mapToBean(map, clazz));
		}
		return result;
	}
	
	public static <T> List<Map<String, T>> jsonToListMap(String jsonStr, Class<T> clazz) throws JsonMappingException, JsonProcessingException
	{
		List<Map<String, T>> list = (List<Map<String, T>>) objectMapper.readValue(jsonStr,
				new TypeReference<List<Map<String, T>>>() {
				});
		return list;
	}
	
	/** 
	* @Title: jsonToList 
	* @Description: json数组转list，基本类型
	* @param jsonArrayStr
	* @param clazz
	* @return  参数说明 
	* @return List<T>    返回类型 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	* 
	*/
	public static <T> List<T> jsonToList(String jsonArrayStr, Class<T> clazz) throws JsonMappingException, JsonProcessingException
	{
		return objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {});
	}

	/**
	 * map转化对象
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T mapToBean(Map map, Class<T> clazz) {
		return objectMapper.convertValue(map, clazz);
	}
	
	public static <T> T objectToBean(Object object, Class<T> clazz) {
		return objectMapper.convertValue(object, clazz);
	}

}
