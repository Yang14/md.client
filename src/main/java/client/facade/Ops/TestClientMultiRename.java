package client.facade.Ops;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMultiRename extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestClientMultiRename");


    @Before
    public void setUp() throws RemoteException {
        super.setUp();
    }

    @Test
    public void testMultiRename() throws InterruptedException {
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
        logger.info(String.format("rename dir, thread count is %s time: %s", threadCount, (end - start)));
    }

    public void testMultiRenameFile() throws InterruptedException {
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
        logger.info(String.format("rename file, thread count is %s time: %s", threadCount, (end - start)));
    }

    private void renameSubDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.renameDir(parentDir, "dir" + i, "r-dir" + i);
        }
    }

    private void renameSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.renameFile(parentDir, "file" + i, "r-file" + i);
        }
    }

}
