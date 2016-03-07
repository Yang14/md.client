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
//                    listDir("/" + Thread.currentThread().getName());
                    clientService.listDir("/" + Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("list dir count %s, thread count is %s time: %s",count, threadCount, (end - start)));
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
        logger.info(String.format("find file count %s, thread count is %s time: %s",count, threadCount, (end - start)));
    }
    private void listDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.listDir(parentDir+ "/dir" + i);
        }
    }

    private void findFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.findFileMd(parentDir, "file" + i);
        }
    }

    public void testListDir() throws RemoteException {
        printMdList(clientService.listDir("/t2"));
//        for (int i = 0; i < count; i++) {
//            clientService.renameDir("/t1", "dir" + i, "r-dir" + i);
//        }
//        printMdList(clientService.listDir("/t1"));
//        logger.info(clientService.findFileMd("/","t1").toString());
    }
}
