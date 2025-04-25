package jim.framework.jwt;

public class JWTData {

	private String token;
	
	private String secret;

	public JWTData(String secret, String token) {
		this.secret = secret;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	
	
}
