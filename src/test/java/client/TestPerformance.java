package client;

import client.perform.TestClientMultiCreate;
import client.perform.TestClientMultiDel;
import client.perform.TestClientMultiFind;
import client.perform.TestClientMultiRename;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestPerformance {
    private static Logger logger = LoggerFactory.getLogger("TestPerformance");

    private ClientService clientService = new ClientServiceImpl();

    @Test
    public void testDelFile() throws RemoteException, InterruptedException {
        int[] countArray = new int[]{1,2,4,8,16,64};
        for (int i=0;i<countArray.length;i++){
            testWithThreadCount(countArray[i]);
        }
    }

    private void testWithThreadCount(int count) throws RemoteException, InterruptedException {
        logger.info(String.format("--------------begin test with %s-----------------",count));
        new TestClientMultiCreate(count).testMultiCreate();
        new TestClientMultiFind(count).testMultiFind();
        new TestClientMultiRename(count).testMultiRename();
        new TestClientMultiDel(count).testMultiDel();
        logger.info(String.format("--------------end test -----------------"));
    }

}
