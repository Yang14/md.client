package client;

import client.facade.ops.BaseMultiMdTest;
import client.service.tool.JedisPoolUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestRedis extends BaseMultiMdTest {
    static Logger logger = LoggerFactory.getLogger("TestFunction");
    String channelStr = "md_address_list";
    @Before
    public void setUp() {
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.subscribe(new MDSubscribe(), channelStr);
    }

    @Test
    public void testRedisPool() {
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.set("jd", "1");
        logger.info(jedis.get("linux"));
        JedisPoolUtils.returnResource(jedis);
    }

    @Test
    public void testRedisPubSub(){
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.publish(channelStr,"192.168.0.12");
    }
}
