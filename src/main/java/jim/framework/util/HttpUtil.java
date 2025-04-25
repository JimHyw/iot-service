package jim.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Http请求类
 */
public class HttpUtil {
	private static HttpClientBuilder httpClientBuilder = HttpClientBuilder
			.create().setDefaultCookieStore(new BasicCookieStore());
	private static CloseableHttpClient httpClient;
	private static RequestConfig REQUEST_CONFIG = RequestConfig.custom()
			.setConnectionRequestTimeout(5000).setConnectTimeout(5000)
			.setSocketTimeout(5000).build();

	public HttpUtil(CloseableHttpClient httpClient) {
		super();
		HttpUtil.httpClient = httpClient;
	}

	public static HttpUtil newInstance() {
		httpClient = httpClientBuilder.build();
		return new HttpUtil(httpClient);
	}

	public static HttpUtil newSSLInstance() {
		// 采用绕过验证的方式处理https请求
		SSLContext sslcontext;
		try {
			sslcontext = createIgnoreVerifySSL();

			// 设置协议http和https对应的处理socket链接工厂的对象
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https",
							new SSLConnectionSocketFactory(sslcontext)).build();
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			HttpClients.custom().setConnectionManager(connManager);

			// 创建自定义的httpclient对象
			httpClient = HttpClients.custom().setConnectionManager(connManager).build();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HttpUtil(httpClient);
	}

	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private static SSLContext createIgnoreVerifySSL()
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}

	public String doPost(String url) throws Exception {
		return doPost(url, null);
	}
	
	public String doPost(String url, Map<String, String> params)
			throws Exception {
		return doPost(url, params, null);
	}

	public String doPostJson(String url, Object params)
			throws Exception {
		return doPostJson(url, params, null);
	}
	
	public String doPostJson(String url, Object params, Map<String, String> header) throws Exception {
		HttpPost request = new HttpPost(url);
		if (params != null) {
			 //装填参数
	        StringEntity s = new StringEntity(JSONObject.toJSONString(params), "utf-8");
	        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
	                "application/json"));
	        //设置参数到请求对象中
	        request.setEntity(s);
	 
	        //设置header信息
	        //指定报文头【Content-type】、【User-Agent】
	        request.addHeader("Content-type", "application/json");
	        request.addHeader("Accept", "application/json");
		}
		if(header != null) {
        	Iterator<Entry<String, String>> iter = header.entrySet().iterator();
        	while(iter.hasNext()) {
        		Entry<String, String> entry = iter.next();
        		request.addHeader(entry.getKey(), entry.getValue());
        	}
        }

		return excute(request);
	}
	
	public String doPost(String url, Map<String, String> params,  Map<String, String> header) throws Exception {
		HttpPost request = new HttpPost(url);
		if (params != null && params.size() > 0) {
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				if (params.get(key) != null) {
					nvp.add(new BasicNameValuePair(key, params.get(key)));
				}
			}
			request.setEntity(new UrlEncodedFormEntity(nvp, "utf-8"));
			
		}
		
		if(header != null) {
        	Iterator<Entry<String, String>> iter = header.entrySet().iterator();
        	while(iter.hasNext()) {
        		Entry<String, String> entry = iter.next();
        		request.addHeader(entry.getKey(), entry.getValue());
        	}
        }
		return excute(request);
	}

	public  String doGet(String url) throws Exception {
		return doGet(url, null);
	}

	public String doGet(String url, Map<String, String> params)
			throws Exception {
		return doGet(url, params, null);
	}
	
	public String doGet(String url, Map<String, String> params, Map<String, String> header)
			throws Exception {
		StringBuilder sb = null;
		if (params != null && params.size() > 0) {
			sb = new StringBuilder();
			for (String key : params.keySet()) {
				if (params.get(key) != null) {
					sb.append(key);
					sb.append("=");
					sb.append(params.get(key));
					sb.append("&");
				}
			}
		}

		HttpGet request = null;
		if (sb == null || sb.length() == 0) {
			request = new HttpGet(url);
		} else {
			if (url.indexOf("?") < 0)
				sb.insert(0, "?");
			sb.deleteCharAt(sb.length() - 1);
			request = new HttpGet(url + sb.toString());
		}
		if(header != null) {
        	Iterator<Entry<String, String>> iter = header.entrySet().iterator();
        	while(iter.hasNext()) {
        		Entry<String, String> entry = iter.next();
        		request.addHeader(entry.getKey(), entry.getValue());
        	}
        	request.addHeader("Content-type", "application/json");
	        request.addHeader("Accept", "application/json");
        }
		return excute(request);
	}

	private String excute(HttpRequestBase request) throws Exception {
		CloseableHttpResponse response = null;
		try {
			request.setConfig(REQUEST_CONFIG);
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				return EntityUtils.toString(response.getEntity());
			} else {

//				String result = EntityUtils.toString(response.getEntity(), "UTF-8");
				//return result;
				throw new Exception("HTTP: " + status);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != response)
				response.close();
		}
	}

	public void downLoadFile(String url, String pathFile) throws Exception {
		HttpGet request = new HttpGet(url);
		CloseableHttpResponse response = null;
		InputStream is = null;
		FileOutputStream os = null;
		try {
			request.setConfig(REQUEST_CONFIG);
			response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				is = response.getEntity().getContent();
				os = new FileOutputStream(new File(pathFile));
				int n = 0;
				byte b[] = new byte[1024];
				while ((n = is.read(b)) != -1) {
					os.write(b, 0, n);
				}
				os.flush();
			} else {
				throw new Exception("HTTP: " + status);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != os)
				os.close();
			if (null != response)
				response.close();
		}
	}

	public static boolean isTimeout(Exception e) {
		if (e instanceof ConnectTimeoutException
				|| e instanceof SocketTimeoutException) {
			return true;
		}
		return false;
	}
	
	public static String sendJsonPost(String url, Object data) throws ParseException, IOException {
		return sendJsonPost(url, JSONObject.toJSONString(data), null, "utf-8");
	}
	
	public static String sendJsonPost(String url, JSONObject jsonObject,String encoding) throws ParseException, IOException {
		return sendJsonPost(url, jsonObject.toJSONString(), null, "utf-8");
	}
	
	public static String sendJsonPost(String url, Object data, Map<String, String> header) throws ParseException, IOException {
		return sendJsonPost(url, JSONObject.toJSONString(data), header, "utf-8");
	}
	
	/**
     * 发送post请求
     * @param url  路径
     * @param jsonObject  参数(json类型)
     * @param encoding 编码格式
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String sendJsonPost(String url, String jsonData, Map<String, String> header, String encoding) throws ParseException, IOException {
        String body = "";
 
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
 
        //装填参数
        StringEntity s = new StringEntity(jsonData, "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
 
        //设置header信息
        httpPost.addHeader("Content-type", "application/json");
        httpPost.addHeader("Accept", "application/json");

        if(header != null) {
        	Iterator<Entry<String, String>> iter = header.entrySet().iterator();
        	while(iter.hasNext()) {
        		Entry<String, String> entry = iter.next();
        		httpPost.addHeader(entry.getKey(), entry.getValue());
        	}
        }
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }
}
