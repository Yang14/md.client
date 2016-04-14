package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiDel extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("ClientMultiDel");
    private CountDownLatch gate = new CountDownLatch(1);

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
//        testMultiDelFile();
        latchForOps.countDown();
        testMultiDelDir();
    }

    public void testMultiDelDir() throws InterruptedException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    gate.await();
                    clientService.deleteDir("/", Thread.currentThread().getName());
//                    delDir("/"+ Thread.currentThread().getName());
                    latchDir.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        long start = System.currentTimeMillis();
        gate.countDown();
        latchDir.await();
        long end = System.currentTimeMillis();
        int lcount = count * threadCount;
        logger.info(String.format("del dir: %s    %s    %s",
                lcount,  df.format(lcount * 1000.0 / (end - start)),(end -start)));
        gate = new CountDownLatch(1);
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
        int count = fileI*fileJ * threadCount;
        logger.info(String.format("del file: %s    %s", count, df.format( count * 1000.0 / (end - start))));
    }

    private void delDir(String parentDir) throws RemoteException {
        for (int i = 0; i < dirI; i++) {
            for (int j = 0; j < dirJ; j++) {
                clientService.deleteDir(parentDir + "/rd" + i + threadCount, "rr-dir"+j);
            }
        }
    }

    private void delFile(String parentDir) throws RemoteException {
        for (int i = 0; i < fileI; i++) {
            for (int j = 0; j < fileJ; j++) {
                clientService.deleteFile(parentDir + "/d" + i + threadCount, "r-file" + j);
            }
        }
    }

}
