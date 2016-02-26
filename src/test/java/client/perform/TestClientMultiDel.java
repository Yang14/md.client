package client.perform;

import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMultiDel {
    private static Logger logger = LoggerFactory.getLogger("TestClientMultiDel");

    private ClientService clientService = new ClientServiceImpl();

    @Test
    public void testClearMd() throws InterruptedException, RemoteException {
        long start = System.currentTimeMillis();
        clientService.deleteDir("/");
        long end = System.currentTimeMillis();
        logger.info(String.format("delete ok, thread count is %s time: %s",
                (end - start)));
    }

}
