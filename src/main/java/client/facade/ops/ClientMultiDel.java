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
        logger.info(String.format("del dir count %s, thread count is %s time: %s",count, threadCount, (end - start)));
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
        logger.info(String.format("del file count %s, thread count is %s time: %s",count, threadCount, (end - start)));
    }

    private void delDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.deleteDir(parentDir, "r-dir" + i);
        }
    }

    private void delFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.deleteFile(parentDir, "r-file" + i);
        }
    }

}