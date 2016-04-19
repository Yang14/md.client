package client;

import client.service.impl.ClientServiceImplV2;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestHashBase {
    private static Logger logger = LoggerFactory.getLogger("TestHashBase");

    ClientServiceImplV2 serviceImpl = new ClientServiceImplV2();

    @Test
    public void testRenameDir() throws RemoteException, InterruptedException {
        int[] countArray = new int[]{10, 30, 50/*, 100, 300,500,1000*/};
        String oldName = "hashPath";
        String newName = "hashPath2";
        for (int aCountArray : countArray) {
            serviceImpl.createFile(oldName, aCountArray);
            long start = System.currentTimeMillis();
            serviceImpl.renameDir(oldName, newName);
            long end = System.currentTimeMillis();
            logger.info(aCountArray + " " + (end - start));
            serviceImpl.clearHashBase();
        }
    }

}
