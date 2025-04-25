//package jim.framework.util;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Unmarshaller;
//import java.io.Reader;
//import java.io.StringReader;
//
///**
// * @author DanielHyw
// * @ClassName: XMLUtil
// * @Description: XML工具
// * @date Jul 21, 2020 5:20:15 PM
// */
//public class XMLUtil {
//
//    /**
//     * 将XML转为指定的POJO对象
//     *
//     * @param clazz  需要转换的类
//     * @param xmlStr xml数据
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T xmlStrToObject(Class<T> clazz, String xmlStr) throws Exception {
//        T xmlObject = null;
//        Reader reader = null;
//        //利用JAXBContext将类转为一个实例
//        JAXBContext context = JAXBContext.newInstance(clazz);
//        //XMl 转为对象的接口
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//        reader = new StringReader(xmlStr);
//        xmlObject = (T) unmarshaller.unmarshal(reader);
//        if (reader != null) {
//            reader.close();
//        }
//        return xmlObject;
//    }
//
//}
