package redis.seckill;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

//完成秒杀抢购
public class SecKillRedis {

    //测试能否联通redis
    @Test
    public void test() {
        Jedis jedis = new Jedis("192.168.220.201", 6379);
        jedis.auth("123456");
        String ping = jedis.ping();
        System.out.println(ping);
        jedis.close();
    }

    //秒杀

    /**
     * @param uid      用户id，后台随机生成
     * @param ticketNo 票的编号，比如北京-成都的ticketNo，就是bj_cd
     * @return
     */
    public static boolean doSecKill(String uid, String ticketNo) {
        // 非空校验
        if (uid == null || ticketNo == null) {
            return false;
        }

        //连接到jedis，得到jedis对象
        //Jedis jedis = new Jedis("192.168.220.201", 6379);
        //jedis.auth("123456");

        //通过连接池获取到jedis对象
        Jedis jedis = JedisPoolUtils.getJedisPoolInstance().getResource();

        //拼接票的库存key
        String stockKey = "sk:" + ticketNo + ":ticket";

        //拼接秒杀用户要存放到set集合的key，这个set集合可以存放多个userId
        String userKey = "sk:" + ticketNo + ":user";

        //监控库存
        jedis.watch(stockKey);

        //获取对应的票的库存
        String stock = jedis.get(stockKey);
        if (stock == null) {
            System.out.println("秒杀还未开始");
            jedis.close();//如果这个连接是从连接池获取的，这段代码就是将这个连接释放回连接池
            return false;
        }

        //判断用户是否重复秒杀
        if (jedis.sismember(userKey, uid)) {
            System.out.println(uid + "：不能复购");
            jedis.close();
            return false;
        }

        //判断火车票是否有剩余
        if (Integer.parseInt(stock) <= 0) {
            System.out.println("票已经卖完了，秒杀结束");
            jedis.close();
            return false;
        }

        //可以购买
        //库存减一
        //jedis.decr(stockKey);
        ////将该用户添加到抢票成功对应的set集合中
        //jedis.sadd(userKey, uid);

        //使用事务完成秒杀
        //组队
        Transaction multi = jedis.multi();
        multi.decr(stockKey);//减去票的库存
        multi.sadd(userKey, uid);//将该用户添加到抢票成功对应的set集合中
        List<Object> result = multi.exec();
        if (result == null || result.size() == 0){
            System.out.println("抢票失败");
            jedis.close();
            return false;
        }

        System.out.println(uid + "：秒杀成功");
        jedis.close();
        return true;
    }

}
