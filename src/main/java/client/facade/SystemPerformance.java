package client.facade;

import client.facade.ops.ClientMultiCreate;
import client.facade.ops.ClientMultiDel;
import client.facade.ops.ClientMultiFind;
import client.facade.ops.ClientMultiRename;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class SystemPerformance {
    private static Logger logger = LoggerFactory.getLogger("SystemPerformance");
    public static int threadCount;

    public static void testPerformance() throws RemoteException, InterruptedException {
        int[] countArray = new int[]{1, 2, 4, 8, 16, 32, 64};
        for (int i = 0; i < countArray.length; i++) {
            threadCount = countArray[i];
            testWithThreadCount(threadCount);
        }
    }

    private static void testWithThreadCount(int count) throws RemoteException, InterruptedException {
        logger.info(String.format("--------------begin test with %s-----------------", count));
        new ClientMultiCreate().testMultiCreate();
        new ClientMultiFind().testMultiFind();
        new ClientMultiRename().testMultiRename();
        new ClientMultiDel().testMultiDel();
        logger.info(String.format("--------------end test -----------------"));
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {
        testPerformance();
    }

}
