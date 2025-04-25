package jim.framework.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author DanielHyw
 * @date 2024年08月19日 15:11
 */
public class Base64OrMultipartFile implements MultipartFile {
    private final byte[] imgContent;
    private final String header;

    public Base64OrMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }

    //base64转为MultipartFile
    public static MultipartFile base64ToMultipart(String base64) {
        String[] baseStrs = base64.split(",");
        byte[] b;
        b = baseStrs.length > 1 ? Base64.decodeBase64(baseStrs[1]) : Base64.decodeBase64(baseStrs[0]);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        String header = baseStrs.length > 1 ? baseStrs[0] : "data:image/png;base64";
        return new Base64OrMultipartFile(b, header);
    }


    //网络文件转Base64--这个有问题
    public static String fileToBase64(String urlStr) {
        String base64 = "";
        try {
            //把地址转换成URL对象
            URL url = new URL(urlStr);
            //创建http链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream in = conn.getInputStream();
            //截取链接中的文件名
            String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
            //请求OSS方法

            //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
            byte[] data = null;
            data = new byte[in.available()];
            in.read(data);
            in.close();
            //对字节数组Base64编码
            java.util.Base64.Encoder base64Encoder = java.util.Base64.getMimeEncoder();
            base64 = base64Encoder.encodeToString(data).replaceAll("[\\s*\t\n\r]", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;//返回Base64编码过的字节数组字符串
    }

    /**
     * 根据http地址获取图片base64
     * @param imgURL
     * @return
     */
    public static String getImageStrFromUrl(String imgURL) {
        byte[] data = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imgURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            data = outStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (data == null) {
            return "";
        }

        java.util.Base64.Encoder base64Encoder = java.util.Base64.getMimeEncoder();
        String base64 = base64Encoder.encodeToString(data).replaceAll("[\\s*\t\n\r]", "");

        return base64;
    }


    //本地文件转为base64
    public static String fileToBase64(File videofilePath) {
        long size = videofilePath.length();
        byte[] imageByte = new byte[(int) size];
        FileInputStream fs = null;
        BufferedInputStream bis = null;
        try {
            fs = new FileInputStream(videofilePath);
            bis = new BufferedInputStream(fs);
            bis.read(imageByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return java.util.Base64.getEncoder().encodeToString(imageByte).replaceAll("==", "");
    }


    //  MultipartFile转为base64
    public static String multipartToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        byte[] imageBytes = null;
        String base64EncoderImg = "";
        try {
            imageBytes = file.getBytes();
            java.util.Base64.Encoder base64Encoder = java.util.Base64.getMimeEncoder();
            base64EncoderImg = base64Encoder.encodeToString(imageBytes).replaceAll("[\\s*\t\n\r]", "");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return base64EncoderImg;
    }
}
