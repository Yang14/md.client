package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiFind extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestClient");

    public ClientMultiFind() {
        try {
            super.setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void testMultiFind() throws InterruptedException, RemoteException {
        testMultiListDir();
        latchForOps.countDown();
        testMultiFindFile();
    }

    public void testMultiListDir() throws InterruptedException, RemoteException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    listDir("/" + Thread.currentThread().getName());
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
        int count = dirI*dirJ * threadCount;
        logger.info(String.format("find dir: %s    %s", count, df.format(count * 1000.0 / (end - start))));
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
        int count = fileI*fileJ * threadCount;
        logger.info(String.format("find file: %s    %s", count,  df.format(count * 1000.0 / (end - start))));
    }

    private void listDir(String parentDir) throws RemoteException {
        String path = "";
        String temp;
        for (int i = 0; i < dirI; i++) {
            temp = "s" + i + threadCount;
            //clientService.listDir(parentDir + path + "/" + temp);
            path += "/" + temp;
            for (int j = 0; j < dirJ; j++) {
                clientService.listDir(parentDir + path + "/" + "dir" + j);
            }
        }
    }

    private void findFile(String parentDir) throws RemoteException {
        for (int i = 0; i < fileI; i++) {
            for (int j = 0; j < fileJ; j++) {
                clientService.findFileMd(parentDir + "/d" + i + threadCount, "file" + j);
            }
        }
    }

}
