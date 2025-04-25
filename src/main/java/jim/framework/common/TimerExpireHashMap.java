package jim.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
 
/** 
* @ClassName: TimerExpireHashMap 
* @Description: 会动态删除的map容器
* @author DanielHyw
* @date Jan 11, 2021 6:11:00 PM 
* 
* @param <K>
* @param <V> 
*/
public class TimerExpireHashMap<K, V> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 1L;

	// 定时删除过期键，间隔时间
    private static final long CHECK_TIME_SECOND = 1 * 1000;
 
    // 定时器
    private Timer timer = new Timer();
 
    // 期时间容器
    private Map<K, Long> timerMap = new HashMap<>();
 
    // 过期数据回调
    private TimerExpireHashMapCallback<K, V> timerExpireHashMapCallback;
    
    private boolean running = false;
    
    @Override
    protected void finalize()
    {
    	if(running) {
            timer.cancel();
        }
    }
 
    /**
     * 定时删除过期键
     */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
        	if(timerMap.isEmpty()) {
                return;
            }

        	try {
        		long currentTime = System.currentTimeMillis();
                List<K> keys = new ArrayList<>(timerMap.keySet());
                for(K key : keys) {
                	Long keyTime = timerMap.get(key);
                	if (currentTime >= keyTime.longValue()) {
                        if(timerExpireHashMapCallback != null) {
                            try {
                                timerExpireHashMapCallback.callback(key, getEx(key));
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                        remove(key);
                        timerMap.remove(key);
                    }
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    };
 
    /**
     * 构造方法
     * @param initialCapacity   容器初始数量
     * @param loadFactor        随机因子
     */
    public TimerExpireHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
 
    /**
     * 构造方法
     * @param initialCapacity   容器初始数量
     */
    public TimerExpireHashMap(int initialCapacity) {
        super(initialCapacity);
    }
 
    /**
     * 构造方法
     */
    public TimerExpireHashMap() {
        super();
    }
 
    /**
     * 构造方法
     * @param map
     */
    public TimerExpireHashMap(Map<K, V> map) {
        super(map);
    }

 
    /**
     * 获取数据
     * @param key
     */
    @Override
    public V get(Object key) {
        Long expireTime = checkKeyExpireTime(key);
        if (expireTime == null || expireTime > 0) {
            return super.get(key);
        }
        return null;
    }
    
    @Override
    public V remove(Object key) {
    	timerMap.remove(key);
    	return super.remove(key);
    }
    
    private V getEx(Object key) {
    	return super.get(key);
    }
 
    /**
     * 放入数据
     * @param key           键值
     * @param value         数据
     * @param expireSecond  过期时间（秒）
     * @return  数据
     */
    public V put(K key, V value, Long expireSecond) {
        if(expireSecond != null && expireSecond.longValue() > 0) {
            setKeyExpireTime(key, expireSecond, false);
        }
        
        
        return super.put(key, value);
    }
 
    /**
     * 返回key过期剩余时间（秒）
     * @param key   键值
     * @return      返回key过期剩余时间（秒）
     */
    public Long checkKeyExpireTime(Object key) {
        Long second = timerMap.get(key);
        if(second == null) {
            return null;
        }
        long currentTime = System.currentTimeMillis();
        return ((second.longValue() - currentTime) / 1000);
    }
 
    /**
     * 为键值设置过期时间
     * @param key               键值
     * @param expireSecond      过期时间（秒）
     */
    public void setKeyExpireTime(K key, Long expireSecond) {
    	setKeyExpireTime(key, expireSecond, true);
    }
    
    private void setKeyExpireTime(K key, Long expireSecond, boolean checkKey) {
        if(!checkKey || (expireSecond != null && expireSecond.longValue() > 0 &&  this.containsKey(key))) {
            long currentTime = System.currentTimeMillis();
            long expireTime = currentTime + (expireSecond * 1000);
            timerMap.put(key, expireTime);
            
            if(!running) {
            	running = true;
            	timer.schedule(timerTask, CHECK_TIME_SECOND, CHECK_TIME_SECOND);
            }
        }
    }
 
    /**
     * 设置过期数据设置监听
     * @param timerExpireHashMapCallback    监听回调
     */
    public void setTimerExpireHashMapCallback(TimerExpireHashMapCallback<K, V> timerExpireHashMapCallback) {
        this.timerExpireHashMapCallback = timerExpireHashMapCallback;
    }
 
    /**
     * 数据设置回调
     * @param <K>
     * @param <V>
     */
    public interface TimerExpireHashMapCallback<K, V> {
        /**
         * 监听回调
         * @param key   过期键
         * @param value 过期值
         * @throws RuntimeException
         */
        public void callback(K key, V value) throws RuntimeException;
    }
}