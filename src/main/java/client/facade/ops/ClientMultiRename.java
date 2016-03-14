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
            for (int i = 0; i < threadCount; i++) {
                for (int j = 0; j < 100; j++) {
                    String dirName = "rd" + j + threadCount;
                    clientService.createDirMd("/" + threadNameArray[i], dirName,
                            getMdAttr(dirName, 99, true));
                }
            }
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
        int count = dirI*dirJ * threadCount;
        logger.info(String.format("rename dir: %s    %s", count,  df.format(count * 1000.0 / (end - start))));
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
        int count = fileI*fileJ * threadCount;
        logger.info(String.format("rename file: %s    %s", count,  df.format(count * 1000.0 / (end - start))));
    }

    private void renameSubDir(String parentDir) throws RemoteException {
        for (int i = 0; i < dirI; i++) {
            for (int j = 0; j < dirJ; j++) {
                clientService.renameDir(parentDir + "/rd" + i + threadCount,
                        "r-dir" + j, "rr-dir" +j);
            }
        }
    }

    private void renameSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < fileI; i++) {
            for (int j = 0; j < fileJ; j++) {
                clientService.renameFile(parentDir + "/d" + i + threadCount, "file" + j, "r-file" + j);
            }
        }
    }

    public void operatorForRename() throws RemoteException, InterruptedException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    createDirForRename("/" + Thread.currentThread().getName());
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
    }

    private void createDirForRename(String parentDir) throws RemoteException {
        for (int i = 0; i < dirI; i++) {
            for (int j = 0; j < dirJ; j++) {
                clientService.createDirMd(parentDir + "/rd" + i + threadCount,
                        "r-dir" + j, getMdAttr("r-dir" + j, j, false));
            }
        }
    }


}
