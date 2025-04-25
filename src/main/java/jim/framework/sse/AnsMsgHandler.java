package jim.framework.sse;

import java.io.InputStream;

public interface AnsMsgHandler {

	void actMsg(InputStream is, String line);
	
}
