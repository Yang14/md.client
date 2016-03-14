package client.facade.ops;

import base.md.MdAttr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiFind extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestClient");
    private CountDownLatch latch = new CountDownLatch(1);

    public ClientMultiFind() {
        try {
            super.setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void testMultiFind() throws InterruptedException, RemoteException {
        operatorForListDir();
        testMultiListDir();
        latchForOps.countDown();
        testMultiFindFile();
    }

    public void operatorForListDir() throws InterruptedException, RemoteException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildDirAndFile("/f" + Thread.currentThread().getName());
                    latchPer.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchPer.await();
        latch.countDown();
        logger.info(String.format("pre dir for list"));
    }

    public void testMultiListDir() throws InterruptedException, RemoteException {
        latch.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    listDir("/f" + Thread.currentThread().getName());
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
        int count = 1000 * threadCount;
        logger.info(String.format("find dir: %s    %s    %s", count, count * 1000.0 / (end - start), (end - start)));
    }

    public void testMultiFindFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    findFile("/" + Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("find file: %s    %s", count, count * 1000.0 / (end - start)));
    }

    private void buildDirAndFile(String parentDir) throws RemoteException {
        String path = "";
        String temp = "";
        for (int i = 0; i < 10; i++) {
            temp = "fs" + i + threadCount;
            clientService.createDirMd(parentDir + path, temp, getMdAttr(temp, i, true));
            path += "/" + temp;
            for (int j = 0; j < 100; j++) {
                clientService.createDirMd(parentDir + path, "f-dir" + j, getMdAttr("f-dir" + j, i, true));
            }
        }
    }

    private void listDir(String parentDir) throws RemoteException {
        /*String path = "";
        String temp = "";
        for (int i = 0; i < 10; i++) {
            temp = "fs" + i+ threadCount;
            clientService.listDir(parentDir + path + "/" + temp);
            path += "/" + temp;
            for (int j = 0; j < 100; j++) {
                clientService.listDir(parentDir + path + "/" + "f-dir" + j);
            }
        }*/
        List<MdAttr> mdAttrs = clientService.listDir(parentDir);
        for (MdAttr mdAttr : mdAttrs) {
            if (mdAttr.getType()) {
                listDir(parentDir + "/" + mdAttr.getName());
            }
        }
    }

    private void findFile(String parentDir) throws RemoteException {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 1000; j++) {
                clientService.findFileMd(parentDir + "/d" + i + threadCount, "file" + j);
            }
        }
    }

}
