package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiCreate extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("ClientMultiCreate");

    public ClientMultiCreate() {
        try {
            setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUp() throws RemoteException {
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            clientService.createDirMd("/", threadName, getMdAttr(threadName, 5, true));
            //clientService.createDirMd("/", threadName + "-forFile", getMdAttr(threadName + "-forFile", 99, false));
            name[i] = threadName;
        }
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < 100; j++) {
                String threadName = "t" + i;
                clientService.createDirMd("/", threadName + "-forFile" + j, getMdAttr(threadName + "-forFile", 99, false));
            }
        }
        threadNameArray = name;
    }

    public void testMultiCreate() throws InterruptedException, RemoteException {
        testMultiCreateDir();
        latchForOps.countDown();
        testMultiCreateFile();
    }

    public void testMultiCreateDir() throws InterruptedException, RemoteException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSubDir("/" + Thread.currentThread().getName());
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
        logger.info(String.format("create dir count %s, thread count is %s time: %s", count, threadCount, (end - start)));
    }

    public void testMultiCreateFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSubFile("/" + Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("create file count %s, thread count is %s time: %s", count, threadCount, (end - start)));
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createDirMd(parentDir, "dir" + i, getMdAttr("dir" + i, i, true));
        }
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < 1000; j++) {
                clientService.createFileMd(parentDir + i, "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }

    public void testListDir() throws RemoteException {
        long start = System.currentTimeMillis();
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/", "d3", getMdAttr("d3", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 1, true));
        logger.info(clientService.listDir("/d1").toString());
        clientService.deleteDir("/", "d1");
        logger.info(clientService.listDir("/d1").toString());
        logger.info(clientService.listDir("/").toString());
        long end = System.currentTimeMillis();
        logger.info(String.format("list dir / ok, thread count is %s time: %s", 1, (end - start)));
    }

    public void testRenameDir() throws RemoteException {
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/", "d3", getMdAttr("d3", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 1, true));
        logger.info(clientService.listDir("/d1").toString());
        clientService.renameDir("/", "d1", "r-d1");
        logger.info(clientService.listDir("/r-d1").toString());
        logger.info(clientService.listDir("/").toString());
    }

}
