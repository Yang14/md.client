package client.facade.Ops;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMultiCreate extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestClient");

    @Before
    public void setUp() throws RemoteException {
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 2, true));
        clientService.createDirMd("/d1/d2", "d3", getMdAttr("d3", 3, true));
        clientService.createDirMd("/d1/d2/d3", "d4", getMdAttr("d4", 4, true));
        clientService.createDirMd("/d1/d2/d3/d4", "d5", getMdAttr("d5", 5, true));
        clientService.createDirMd("/d1/d2/d3/d4/d5", "d6", getMdAttr("d6", 5, true));
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            clientService.createDirMd("/", threadName, getMdAttr(threadName, 5, true));
            clientService.createDirMd("/", threadName + "-forFile", getMdAttr(threadName + "-forFile", 99, false));
            name[i] = threadName;
        }
        threadNameArray = name;
    }

    @Test
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
        logger.info(String.format("create dir, thread count is %s time: %s", threadCount, (end - start)));
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
        logger.info(String.format("create file, thread count is %s time: %s", threadCount, (end - start)));
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createDirMd(parentDir, "dir" + i, getMdAttr("dir" + i, i, true));
        }
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createFileMd(parentDir, "file" + i, getMdAttr("file" + i, i, false));
        }
    }

    public void testListDir() throws RemoteException {
        long start = System.currentTimeMillis();
        clientService.createDirMd("/","d1",getMdAttr("d1",1,true));
        clientService.createDirMd("/","d3",getMdAttr("d3",1,true));
        clientService.createDirMd("/d1","d2",getMdAttr("d2",1,true));
        logger.info(clientService.listDir("/d1").toString());
        clientService.deleteDir("/","d1");
        logger.info(clientService.listDir("/d1").toString());
        logger.info(clientService.listDir("/").toString());
        long end = System.currentTimeMillis();
        logger.info(String.format("list dir / ok, thread count is %s time: %s",1,(end - start)));
    }

    public void testRenameDir() throws RemoteException {
        clientService.createDirMd("/","d1",getMdAttr("d1",1,true));
        clientService.createDirMd("/","d3",getMdAttr("d3",1,true));
        clientService.createDirMd("/d1","d2",getMdAttr("d2",1,true));
        logger.info(clientService.listDir("/d1").toString());
        clientService.renameDir("/","d1","r-d1");
        logger.info(clientService.listDir("/r-d1").toString());
        logger.info(clientService.listDir("/").toString());
    }

}
