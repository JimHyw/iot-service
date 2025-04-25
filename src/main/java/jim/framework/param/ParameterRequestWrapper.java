package jim.framework.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String , String[]> params = new HashMap<String, String[]>();

    private final byte[] body;
    
    public ParameterRequestWrapper(HttpServletRequest request) {
        // 将request交给父类，以便于调用对应方法的时候，将其输出，其实父亲类的实现方式和第一种new的方式类似
        super(request);
        //将参数表，赋予给当前的Map以便于持有request中的参数
        Map<String, String[]> requestMap=request.getParameterMap();
        this.params.putAll(requestMap);
        // 从json请求获取参数
    	String bodyStr = getBodyString();
    	body = bodyStr.getBytes(Charset.defaultCharset());
    	JSONObject jsonObject = JSONObject.parseObject(bodyStr);
    	if(jsonObject != null)
    	{
    		Set<String> keys = jsonObject.keySet();
        	for(String key : keys)
        	{
        		this.params.put(key, new String[] {jsonObject.getString(key)});
        	}
    	}
        this.modifyParameterValues();
    }
    
    public String getBodyString() {
        try {
            return inputStream2String(getInputStream());
        } catch (IOException e) {
            //log.error("", e);
            throw new RuntimeException(e);
        }
    }
    
    private String inputStream2String(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            //log.error("", e);
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log.error("", e);
                }
            }
        }

        return sb.toString();
    }
    
    /**
     * 重写getInputStream方法  post类型的请求参数必须通过流才能获取到值
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
    	if(body != null)
    	{
    		final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
    		return new MyServletInputStream(inputStream);
    	}
        //非json类型，直接返回
//    	try {
		if(super.getHeader(HttpHeaders.CONTENT_TYPE) == null || !super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)){
            return super.getInputStream();
        }
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
        
        //为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), "utf-8");
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }
        JSONObject inJson = JSONObject.parseObject(json);
        
        JSONObject trimJson = jsonTrim(inJson);
        if(trimJson == null){
        	return super.getInputStream();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(JSON.toJSONString(trimJson).getBytes("utf-8"));
        return new MyServletInputStream(bis);
    }

    private JSONObject jsonTrim(JSONObject input){
    	if(input == null){
    		return null;
    	}
    	JSONObject returnJson = new JSONObject();
    	try{
            Set<String> set = input.keySet();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                JSONObject value = null;
                try{
                    value  = input.getJSONObject(key);
                }
                catch (Exception ex){

                }
                if(value != null){
                   value = jsonTrim(value);
                   returnJson.put(key, value);
                }
                else {
                    JSONArray array = null;
                    try{
                         array = input.getJSONArray(key);
                    }
                    catch (Exception ex){

                    }
                    if(array != null){
                        JSONArray trimArray = new JSONArray();
                        for (int i = 0; i < array.size(); i++){
                            Object itemValue = array.get(i);
                            String strValue = null;
                            if(itemValue != null){
                                if(itemValue instanceof String){
                                    strValue = itemValue.toString().trim();
                                    trimArray.add(strValue);
                                }
                                else {
                                    trimArray.add(itemValue);
                                }
                            }
                        }
                        returnJson.put(key, trimArray);
                    }
                    else {
                        Object itemValue = input.get(key);
                        String strValue = null;
                        if(itemValue != null){
                            if(itemValue instanceof String){
                                strValue = itemValue.toString().trim();
                                returnJson.put(key, strValue);
                            }
                            else {
                                returnJson.put(key, itemValue);
                            }

                        }
                    }
                }
            }
    		
    	}catch(Exception ex){
    		return null;
    	}
        
        return  returnJson;
    }

    /**
     * 将parameter的值去除空格后重写回去
     */
    public void modifyParameterValues(){
        Set<String> set =params.keySet();
        Iterator<String> it=set.iterator();
        while(it.hasNext()){
            String key= (String) it.next();
            String[] values = params.get(key);
            values[0] = values[0].trim();
            params.put(key, values);
        }
    }
    /**
     * 重写getParameter 参数从当前类中的map获取
     */
    @Override
    public String getParameter(String name) {
        String[]values = params.get(name);
        if(values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }
    /**
     * 重写getParameterValues
     */
    public String[] getParameterValues(String name) {//同上
        return params.get(name);
    }

    class MyServletInputStream extends  ServletInputStream{
        private ByteArrayInputStream bis;
        public MyServletInputStream(ByteArrayInputStream bis){
            this.bis=bis;
        }
        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
        @Override
        public int read() throws IOException {
            return bis.read();
        }
    }
}
