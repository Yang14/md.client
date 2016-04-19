package client;

import client.perform.BaseMultiMdTest;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestFunction extends BaseMultiMdTest {
    static Logger logger = LoggerFactory.getLogger("TestFunction");
    ClientService clientService = new ClientServiceImpl();

    String dirName = "f3-dir";
    String fileName = "f1-file";
    int count = 20;

    @Test
    public void testBalanceFactor() {
        int[] bucketSizeArray = {100, 200, 300, 500, 700, 1000, 10000};
        for (int bucketSize : bucketSizeArray) {
            calFDB(bucketSize);
        }
    }

    @Test
    public void renameDir() throws RemoteException {
        clientService.createDirMd("/", dirName, getMdAttr(dirName, 0, true));
        long start = System.currentTimeMillis();
        clientService.renameDir("/", dirName, dirName + "new");
        long end = System.currentTimeMillis();
        logger.info("" + (end - start));
    }

    private void calFDB(int bucketSize) {
        int dirCount = 1000;
        int dirSize = 1000;
        int mdsNum = 17;

        int[] mdCount = new int[mdsNum];
        for (int i = 0, j = 0, k = 0; i < dirCount * dirSize; i++) {
            mdCount[j] = mdCount[j] + 1;
            if (++k >= bucketSize) {
                if (++j > mdsNum - 1) {
                    j = 0;
                }
                k = 0;
            }
        }/*
        double fdb = 0;
        for (int i = 0; i < mdsNum; i++) {
            fdb += Math.pow((mdCount[i] / (dirCount * dirSize * 1.0)) - 1, 2);
//            logger.info(mdCount[i]+"");
        }
        fdb *= 1.0 / (mdsNum - 1);
        fdb = Math.sqrt(fdb);
        logger.info(bucketSize + "\t\t" + fdb);
        */
        double dx = 0;
        double mean = dirCount * dirSize / (mdsNum * 1.0);
        for (int i = 0; i < mdsNum; i++) {
            dx += Math.pow(1.0 * (mdCount[i] - mean), 2);
//            logger.info(mdCount[i]+" " + dx);
        }
        dx = dx / mdsNum * 1.0;
        dx = Math.sqrt(dx);
        logger.info(bucketSize + "\t\t" + dx + " " + mean);
    }

    @Test
    public void testBucketSize() throws RemoteException {
        clientService.createDirMd("/", dirName, getMdAttr(dirName, 0, true));
        for (int i = 0; i < count; i++) {
            clientService.createFileMd("/" + dirName, fileName + i, getMdAttr(fileName + i, i, false));
        }
        printMdList(clientService.listDir("/" + dirName));
       /* for (int i = 0; i < count; i++) {
            clientService.createDirMd("/", dirName + i, getMdAttr(dirName + i, i, true));
        }*/
    }

    @Test
    public void testList() throws RemoteException {
        printMdList(clientService.listDir("/" + dirName));

    }

    @Test
    public void testClearPart() throws RemoteException {
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 2, true));
        clientService.createDirMd("/d1/d2", "d3", getMdAttr("d3", 3, true));
        clientService.createDirMd("/d1/d2/d3", "d4", getMdAttr("d4", 4, true));
        clientService.createDirMd("/d1/d2/d3/d4", "d5", getMdAttr("d5", 5, true));
        clientService.createDirMd("/d1/d2/d3/d4/d5", "d6", getMdAttr("d6", 5, true));
        logger.info(clientService.listDir("/d1").toString());
        logger.info(clientService.listDir("/d1/d2").toString());
        logger.info(clientService.listDir("/d1/d2/d3").toString());
        logger.info("begin to clean /.");
        long start = System.currentTimeMillis();
//        clientService.deleteDir("/d1","d2");
        long end = System.currentTimeMillis();
        logger.info(String.format("clear dir /,,use time: %s", (end - start)));
    }

    @Test
    public void testClearAll() throws RemoteException {
        testClearPart();
        logger.info("begin to clean /.");
        long start = System.currentTimeMillis();
        clientService.deleteDir("/", "");
        long end = System.currentTimeMillis();
        logger.info(String.format("clear dir /,,use time: %s", (end - start)));

    }

    @Test
    public void testCreateDir() throws RemoteException {
        logger.info("begin test");
        for (int i = 0; i < count; i++) {
            clientService.createDirMd("/", dirName + i, getMdAttr(dirName + i, i, true));
        }
        for (int i = 0; i < count; i++) {
            printMdList(clientService.listDir("/" + dirName + i));
        }
        logger.info("");
        for (int i = 0; i < count; i++) {
            clientService.createFileMd("/" + dirName + i, fileName + i, getMdAttr(fileName + i, i, false));
        }
        for (int i = 0; i < count; i++) {
            printMdList(clientService.listDir("/" + dirName + i));
        }

        logger.info("重命名文件后列表目录");
        for (int i = 0; i < count; i++) {
            clientService.renameFile("/" + dirName + i, fileName + i, fileName + i + "_r");
        }
        logger.info("重命名目录后列表目录");
        for (int i = 0; i < count; i++) {
            clientService.renameDir("/", dirName + i, dirName + i + "_r");
        }
        logger.info("客户端从缓存中得到list目录的地址信息，直接获取mds上的元数据");
        for (int i = 0; i < count; i++) {
            printMdList(clientService.listDir("/" + dirName + i + "_r"));
        }
        logger.info("删除前列出文件");
        for (int i = 0; i < count; i++) {
            logger.info(clientService.findFileMd("/" + dirName + i + "_r", fileName + i + "_r").toString());
        }

        logger.info("删除后列表目录");
        for (int i = 0; i < count; i++) {
            clientService.deleteDir("/", dirName + i + "_r");
        }
        for (int i = 0; i < count; i++) {
            printMdList(clientService.listDir("/" + dirName + i + "_r"));
        }


    }
}
