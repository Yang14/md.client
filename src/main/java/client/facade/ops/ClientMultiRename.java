package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiRename extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("ClientMultiRename");
    private CountDownLatch latch = new CountDownLatch(1);

    public ClientMultiRename() {
        try {
            super.setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void testMultiRename() throws InterruptedException, RemoteException {
        operatorForRename();
        testMultiRenameFile();
        latchForOps.countDown();
        testMultiRenameDir();
    }

    public void testMultiRenameDir() throws InterruptedException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    renameSubDir("/" + Thread.currentThread().getName());
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
        logger.info(String.format("rename dir: %s    %s", count, count * 1000.0 / (end - start)));
    }

    public void testMultiRenameFile() throws InterruptedException {
        latch.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    renameSubFile("/" + Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("rename file: %s    %s", count, count * 1000.0 / (end - start)));
    }

    private void renameSubDir(String parentDir) throws RemoteException {
        String path = "";
        String temp = "";
        String oldTemp = "";
        for (int i = 0; i < 10; i++) {
            temp = "s" + i;
            oldTemp = "r-s" + i;
            clientService.renameDir(parentDir + path, temp, oldTemp);
            path += "/" + oldTemp;
            for (int j = 0; j < 100; j++) {
                clientService.renameDir(parentDir + path, "dir" + j, "r-dir" + j);
            }
        }
    }

    private void renameSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                clientService.renameFile(parentDir + "/rd" + i, "file" + j, "r-file" + j);
            }
        }
    }

    public void operatorForRename() throws RemoteException, InterruptedException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    createDirAndFileForRename("/r" + Thread.currentThread().getName());
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
        logger.info(String.format("pre dir for rename"));
    }

    private void createDirAndFileForRename(String parentDir) throws RemoteException {
        String path = "";
        String temp = "";
        for (int i = 0; i < 10; i++) {
            temp = "s" + i;
            clientService.createDirMd(parentDir + path, temp, getMdAttr(temp, i, true));
            path += "/" + temp;
            for (int j = 0; j < 100; j++) {
                clientService.createDirMd(parentDir + path, "dir" + j, getMdAttr("dir" + j, i, true));
            }
        }

        for (int i = 0; i < 100; i++) {
            clientService.createDirMd(parentDir + "-forFile", "rd" + i, getMdAttr("rd" + i, 99, true));
            for (int j = 0; j < 100; j++) {
                clientService.createFileMd(parentDir + "/rd" + i, "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }


}
