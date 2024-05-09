package redis.seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//使用连接池方式来获取Redis连接
public class JedisPoolUtils {

    //volatile作用：
    //1.保证线程的可见性：当一个线程去修改一个共享变量时，另一个线程可以读取到这个修改的值
    //2.禁止指令重排：保证代码顺序执行
    private static volatile JedisPool jedisPool = null;

    //隐藏构造方法，实现单例
    private JedisPoolUtils() {
    }

    //保证每次调用返回的JedisPool是单例
    public static JedisPool getJedisPoolInstance() {

        //确保并发情况下还是单例-双重校验
        if (jedisPool == null) {
            synchronized (JedisPoolUtils.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    //对连接池进行配置
                    jedisPoolConfig.setMaxTotal(200);
                    jedisPoolConfig.setMaxIdle(32);
                    jedisPoolConfig.setMaxWaitMillis(60 * 1000);
                    jedisPoolConfig.setBlockWhenExhausted(true);
                    jedisPoolConfig.setTestOnBorrow(true);
                    jedisPool = new JedisPool(jedisPoolConfig, "192.168.220.201", 6379, 6000, "123456");
                }
            }
        }
        return jedisPool;
    }

    //释放连接资源
    public static void release(Jedis jedis) {
        if (null != jedis) {
            jedis.close();//如果这个连接是从连接池获取的，这段代码就是将这个连接释放回连接池
        }
    }
}
