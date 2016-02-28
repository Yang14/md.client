package client.perform;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMultiDel extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestClientMultiDel");

    @Before
    public void setUp() throws RemoteException {
        super.setUp();
    }

    @Test
    public void testClearMd() throws InterruptedException, RemoteException {
        long start = System.currentTimeMillis();
        clientService.deleteDir("/", "");
        long end = System.currentTimeMillis();
        logger.info(String.format("delete ok, thread count is %s time: %s", 1, (end - start)));
    }

    @Test
    public void testMultiDel() throws InterruptedException {
        testMultiDelFile();
        latchForOps.countDown();
        testMultiDelDir();
    }

    @Test
    public void testMultiDelDir() throws InterruptedException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    clientService.deleteDir("/", Thread.currentThread().getName());
                    latchDir.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchDir.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("del dir, thread count is %s time: %s", threadCount, (end - start)));
    }

    @Test
    public void testMultiDelFile() throws InterruptedException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    delFile("/" + Thread.currentThread().getName() + "-forFile");
                    latchDir.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchDir.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("del file, thread count is %s time: %s", threadCount, (end - start)));
    }

    private void delFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.deleteFile(parentDir, "file" + i);
        }
    }

}
