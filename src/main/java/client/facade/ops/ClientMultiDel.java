package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiDel extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("ClientMultiDel");

    public ClientMultiDel() {
        try {
            setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUp() throws RemoteException {
        super.setUp();
    }

    public void testMultiDel() throws InterruptedException {
        testMultiDelFile();
        latchForOps.countDown();
        testMultiDelDir();
    }

    public void testMultiDelDir() throws InterruptedException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
//                    clientService.deleteDir("/", Thread.currentThread().getName());
                    delDir("/"+ Thread.currentThread().getName());
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
        int count = 10000 * threadCount;
        logger.info(String.format("del dir: %s    %s", count, count * 1000.0 / (end - start)));
    }

    public void testMultiDelFile() throws InterruptedException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    delFile("/" + Thread.currentThread().getName() + "-forFile");
                    latchFile.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchFile.await();
        long end = System.currentTimeMillis();
        int count = 100000 * threadCount;
        logger.info(String.format("del file: %s    %s", count, count * 1000.0 / (end - start)));
    }

    private void delDir(String parentDir) throws RemoteException {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                clientService.deleteDir(parentDir + "/rd" + i + threadCount, "rr-dir");
            }
        }
    }

    private void delFile(String parentDir) throws RemoteException {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 1000; j++) {
                clientService.deleteFile(parentDir + "/d" + i + threadCount, "r-file" + j);
            }
        }
    }

}
