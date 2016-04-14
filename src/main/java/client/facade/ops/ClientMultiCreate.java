package client.facade.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientMultiCreate extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("ClientMultiCreate");
    private CountDownLatch gate = new CountDownLatch(1);
    public ClientMultiCreate() {
        try {
            super.setUp();
            setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUp() throws RemoteException {
        String[] names = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
                String threadName = "t" +i + threadCount;
                names[i] = threadName;
            clientService.createDirMd("/", threadName, getMdAttr(threadName, 5, true));
            clientService.createDirMd("/", threadName + "-forFile", getMdAttr(threadName + "-forFile", 99, true));
        }
        for(int i=0;i<threadCount;i++){
            for(int j=0;j<fileI;j++){
                String dirName = "d" + j + threadCount;
                String name = "t" + i + threadCount;
                clientService.createDirMd("/" + name + "-forFile",dirName, getMdAttr(dirName, 100, true));
            }
        }
    }

    public void testMultiCreate() throws InterruptedException, RemoteException {
        /*int[] deeps = new int[]{1,3,10,50,70,100};
        for(int deep :deeps) {
            testMultiCreateDir(deep);
        }*/
//        testMultiCreateDir();
        latchForOps.countDown();
        testMultiCreateFile();
    }

    public void testMultiCreateDir() throws InterruptedException, RemoteException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    gate.await();
                    buildSubDir("/" + Thread.currentThread().getName());
//                    forDeep("/" + Thread.currentThread().getName(), deep);
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
//        int count = dirI*dirJ * threadCount;
        int localCount = count * threadCount;
        logger.info(String.format("create dir: %s    %s", localCount,  df.format(localCount*1000.0/(end - start))));
        latchDir = new CountDownLatch(threadCount);
        gate = new CountDownLatch(1);

    }

    public void testMultiCreateFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    gate.await();
                    buildSubFile("/" + Thread.currentThread().getName() + "-forFile");
                    latchFile.countDown();
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
        latchFile.await();
        long end = System.currentTimeMillis();
        int count = fileI*fileJ * threadCount;
        logger.info(String.format("create file: %s    %s", count, df.format(count * 1000.0 / (end - start))));
        gate = new CountDownLatch(1);
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        /*String path = "";
        String temp;
        for (int i = 0; i < dirI; i++) {
            temp = "s" + i + threadCount;
            clientService.createDirMd(parentDir + path, temp, getMdAttr(temp, i, true));
            path += "/" + temp;
            for (int j = 0; j < dirJ; j++) {
                clientService.createDirMd(parentDir + path, "dir" + j, getMdAttr("dir" + j, i, true));
            }
        }*/
       /* for (int i = 0; i < count; i++) {
            clientService.createDirMd(parentDir, "dir" + i, getMdAttr("dir" + i, i, true));
//            clientService.createFileMd(parentDir,"file" + i, getMdAttr("file" + i, i, false));

        }*/
        forTravelAndDelAllDir(parentDir);
//        forRenameDir(parentDir);
    }

    private void forRenameDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createDirMd(parentDir, "dir" + i, getMdAttr("dir" + i, i, true));
        }
    }
    private void forTravelAndDelAllDir(String parentDir) throws RemoteException {
        String path = "";
        String temp;
        for (int i = 0; i < 10; i++) {
            temp = "s" + i + threadCount;
            clientService.createDirMd(parentDir + path, temp, getMdAttr(temp, i, true));
            path += "/" + temp;
            for (int j = 0; j < 10; j++) {
                clientService.createDirMd(parentDir + path, "dir" + j, getMdAttr("dir" + j, i, true));
            }
            for (int j = 0; j < 1000; j++) {
                clientService.createFileMd(parentDir + path, "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }

    private void forDeep(String parentDir,int deep) throws RemoteException {
        String path = "";
        String temp;
        for (int i = 0; i < deep; i++) {
            temp = "s" + i + threadCount+deep;
            clientService.createDirMd(parentDir + path, temp, getMdAttr(temp, i, true));
            path += "/" + temp;
            for (int j = 0; j < 10; j++) {
                clientService.createDirMd(parentDir + path, "dir" + j, getMdAttr("dir" + j, i, true));
            }
            for (int j = 0; j < 100; j++) {
                clientService.createFileMd(parentDir + path, "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < fileI; i++) {
            for (int j = 0; j < fileJ; j++) {
                clientService.createFileMd(parentDir + "/d" + i + threadCount,
                        "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }

}
