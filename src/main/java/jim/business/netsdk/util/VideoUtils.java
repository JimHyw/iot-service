package jim.business.netsdk.util;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jim.framework.util.DateUtil;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Date;

public class VideoUtils {
    private static final Logger log = LoggerFactory.getLogger(VideoUtils.class);
    
    public static String getVideoBufferImage(String videoUrl) {
    	return getVideoBufferImage(videoUrl, null);
    }
    
    public static String getVideoBufferImage(String videoUrl, String localDirPath) {
        //视频帧抓取器
        FFmpegFrameGrabber frameGrabber = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            frameGrabber = new FFmpegFrameGrabber(videoUrl);
            frameGrabber.setOption("rtsp_transport", "tcp");
            //开始
            frameGrabber.start();
            //获取到帧长度
            int length = frameGrabber.getLengthInFrames() == 0 ? 100 : frameGrabber.getLengthInFrames();
            int cutIndex = Math.min(50, length-1);
            int i = 0;
            Frame frame = null;
            while (i < length) {
                // 过滤前10帧,防止获取到黑屏画面
                frame = frameGrabber.grabFrame();
                if (i > cutIndex && frame.image != null) {
                    break;
                }
                ++i;
            }
            //Java2DFrameConverter可以对Frame进行相互转换操作
            BufferedImage srcImage = new Java2DFrameConverter().getBufferedImage(frame);
            int srcImageWidth = srcImage.getWidth();
            int srcImageHeight = srcImage.getHeight();
            // 对截图进行等比例缩放(缩略图)
            int width = 720;
            int height = (int) (((double) width / srcImageWidth) * srcImageHeight);
            BufferedImage thumbnailImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            thumbnailImage.getGraphics().drawImage(srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(thumbnailImage, "jpg", outputStream);
            
            if (localDirPath != null) {
            	// log.info("localDirPath" + localDirPath);
            	Date now = new Date();
            	localDirPath += File.separator + DateUtil.format(now, DateUtil.DATE_PATTERN);
            	File outputDir = new File(localDirPath);
            	if (!outputDir.exists()) {
            		outputDir.mkdirs();
            	}
            	String imageFilePath = localDirPath + File.separator + DateUtil.format(now, "HH点mm分ss秒") + ".jpg";
            	// log.info("imageFilePath" + imageFilePath);
            	File output = new File(imageFilePath);
            	ImageIO.write(srcImage, "jpg", output);
            }

        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            // e.printStackTrace();
        } finally {
            try {
                if (frameGrabber != null) {
                    frameGrabber.stop();
                }
            } catch (FrameGrabber.Exception e) {
            	log.error(e.getMessage(), e);
                // e.printStackTrace();
            }
        }
        if (outputStream.size() == 0) {
        	return null;
        }
        // "data:image/jpg;base64,"
        String imageBase64 = "data:image/jpg;base64," + new String(Base64.getEncoder().encode(outputStream.toByteArray()));

        return imageBase64;
    }
}

