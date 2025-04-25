package jim.framework.jwt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BufferedServletRequestWrapper extends HttpServletRequestWrapper {
	private String requestBody = null;

	public BufferedServletRequestWrapper(HttpServletRequest request) {
		super(request);
		if (requestBody == null) {
			requestBody = readBody(request);
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new CustomServletInputStream(requestBody);
	}

	public String readBody(ServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		try {
			// 使用流
			InputStream inputStream = httpRequest.getInputStream();
			StringBuilder param = new StringBuilder();

			// 输入流作参数传进InputStreamReader并用BufferedReader接受
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine = null;
			// 一直读到空，并设置流编码是UTF8
			while ((inputLine = in.readLine()) != null) {
				param.append(new String(inputLine.getBytes(), "UTF-8"));
			}
			// 记得关闭连接
			in.close();
			return param.toString();
		} catch (IOException e) {
			return null;
		}

	}

	private class CustomServletInputStream extends ServletInputStream {
		private ByteArrayInputStream buffer;

		public CustomServletInputStream(String body) {
			body = body == null ? "" : body;
			this.buffer = new ByteArrayInputStream(body.getBytes());
		}

		@Override
		public int read() throws IOException {
			return buffer.read();
		}

		@Override
		public boolean isFinished() {
			return buffer.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			throw new RuntimeException("Not implemented");
		}
	}

}
