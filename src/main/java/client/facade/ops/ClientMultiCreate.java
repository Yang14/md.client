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
            super.setUp();
            setUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUp() throws RemoteException {
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < 100; j++) {
                String threadName = "t" + i;
                clientService.createDirMd("/"+ threadName + "-forFile","d"+j+threadCount,
                        getMdAttr("d"+j+threadCount, 99, true));
            }
        }
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
        int count = 1000*threadCount;
        logger.info(String.format("create dir: %s    %s",count, count*1000.0/(end - start)));
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
        int count = 100000*threadCount;
        logger.info(String.format("create file: %s    %s",count, count*1000.0/(end - start)));
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        String path = "";
        String temp = "";
        for (int i = 0; i < 10; i++) {
            temp = "s"+i+ threadCount;
            clientService.createDirMd(parentDir+path, temp, getMdAttr(temp,i, true));
            path+="/"+temp;
            for (int j=0;j<100;j++) {
                clientService.createDirMd(parentDir+path, "dir" + j, getMdAttr("dir" + j, i, true));
            }
        }
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 1000; j++) {
                clientService.createFileMd(parentDir +"/d"+ i+threadCount, "file" + j, getMdAttr("file" + j, j, false));
            }
        }
    }

}
