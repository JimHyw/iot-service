package jim.wg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WgApiUtil {
    final static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }


    public static String commMethod(String method, ObjectNode param) {
        String str = "";
        try {
            String url = ConstWg.HOST_URL;
            ObjectMapper mapper = WgApiUtil.getObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", method);

            ArrayNode params = webcmd.putArray("params");
            params.add(param);
            webcmd.put("id", 1001);
            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(url, body);

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                str = "通信失败!";
            } else {
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {

                } else {

                }

                str = mapper.readTree(strResult).toPrettyString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}
