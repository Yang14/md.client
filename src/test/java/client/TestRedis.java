package client;

import client.facade.ops.BaseMultiMdTest;
import client.service.tool.JedisPoolUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestRedis extends BaseMultiMdTest {
    static Logger logger = LoggerFactory.getLogger("TestFunction");

    @Test
    public void testClearPart() throws RemoteException {
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.set("jd", "1");
        logger.info(jedis.get("linux"));
        JedisPoolUtils.returnResource(jedis);
    }

}
