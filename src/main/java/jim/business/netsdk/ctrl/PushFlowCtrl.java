package jim.business.netsdk.ctrl;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @ClassName: PushFlowCtrl 
 * @Description: 推流控制
 * @author DanielHyw
 * @date 2024年1月10日 上午9:32:12 
 *  
 */
public class PushFlowCtrl {
	/** 
	 * 拉流地址
	 */ 
	private String pullUrl = "";
	/** 
	 * 推流地址
	 */ 
	private String pushUrl = "";
	
	private FFmpegFrameGrabber grabber = null;
	
	private FFmpegFrameRecorder recorder = null;
	
	private boolean running = false;
	
	private boolean stoping = false;
	
	private static final Logger log = LoggerFactory.getLogger(HCNetSDKCtrl.class);
	
	private Thread thread = null;
	
	public String getPushUrl() {
		return pushUrl;
	}

	public String getPullUrl() {
		return pullUrl;
	}
	
	public void init(String pullUrl, String pushUrl) {
		log.info("----- do push init");
		if (thread != null)
			stop();
		this.pullUrl = pullUrl;
		this.pushUrl = pushUrl;
		if (thread == null) {
			thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						running = true;
						stoping = false;
						log.info("----- push init:" + getPullUrl());
						grabber = new FFmpegFrameGrabber(getPullUrl());
				        grabber.setOption("rtsp_transport", "tcp");
				        grabber.start();
				        log.info("----- start pull");
				        recorder = new FFmpegFrameRecorder(getPushUrl(),
				                grabber.getImageWidth() >> 1,
				                grabber.getImageHeight() >> 1,
				                grabber.getAudioChannels());

				        int v_rs = 25;

				        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264
				        recorder.setFormat("flv");
				        recorder.setFrameRate(v_rs);
				        recorder.setGopSize(v_rs);
				        recorder.setAudioChannels(grabber.getAudioChannels());
				        FFmpegLogCallback.set();

				        log.info("----- init pull");
				        
				        recorder.start();
				        
				        log.info("----- push start");

				        Frame frame;
				        while (!stoping && null!=(frame=grabber.grab())) {
				            recorder.record(frame);
				        }
				        log.info("----- push ready stop");
				        recorder.close();
				        grabber.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						log.info("----- push stop");
						stoping = false;
						running = false;
					}
				}
			});
		}
	}
	
	public void start() {
		if (pullUrl.isEmpty())
			return;
		if (stoping) {
			// 已经停止中，先等待停止后再启动
			while(thread.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else if (running || thread.isAlive()) {
			return;
		}
		log.info("----- push do start");
		thread.start();
	}
	
	public void stop() {
		if (stoping || !running) {
			return;
		}
		log.info("----- push do stop");
		stoping = true;
		// 等待线程结束
		while(thread.isAlive()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isRunning() {
		return !stoping && running;
	}
}
