package client;

import client.perform.BaseMultiMdTest;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import com.sangupta.murmur.Murmur2;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestFunction extends BaseMultiMdTest {
    static Logger logger = LoggerFactory.getLogger("TestFunction");
    ClientService clientService = new ClientServiceImpl();

    String dirName = "f3-dir";
    String fileName = "f1-file";
    int count = 20;
    private static final int MAX_ELEMENTS = 1000 * 1000;
    private static final List<String> UUIDs = new ArrayList<String>();

    private static final int SEED = 0xB0F57EE3;

    @BeforeClass
    public static void onlyOnce() {
        for(int index = 0; index < MAX_ELEMENTS; index++) {
            UUIDs.add(UUID.randomUUID().toString());
        }
    }

    @Test
    public void testMurmur2() {
        long start = System.currentTimeMillis();

        for(String uuid : UUIDs) {
            byte[] bytes = uuid.getBytes();
            Murmur2.hash(bytes, bytes.length, SEED);
        }

        long end = System.currentTimeMillis();
        System.out.println("Time taken to compute Murmur2 hash: " + (end - start) + "ms.");
    }

    @Test
    public void renameDir() throws RemoteException {
        clientService.createDirMd("/", dirName, getMdAttr(dirName, 0, true));
        long start = System.currentTimeMillis();
        clientService.renameDir("/", dirName, dirName + "new");
        long end = System.currentTimeMillis();
        logger.info("" + (end - start));
    }

    @Test
    public void testBalanceFactor() {
        int[] dirCountArray = {10, 200, 500, 1000};
        int dirSize = 10000;
        int bucketSize = 3000;
        for (int i = 0; i < dirCountArray.length; ++i) {
            calFDB(dirCountArray[i], dirSize, bucketSize);
        }
    }


    private void calFDB(int dirCount, int dirSize, int bucketSize) {
        int mdsNum = 3;
        int[] mdCount = new int[mdsNum];
        for (int i = 0, j = 0, k = 0; i < dirCount * dirSize; i++) {
            mdCount[j] = mdCount[j] + 1;
            if (++k >= bucketSize) {
                if (++j > mdsNum - 1) {
                    j = 0;
                }
                k = 0;
            }
        }
        double dx = 0;
        double mean = dirCount * dirSize / (mdsNum * 1.0);
        for (int i = 0; i < mdsNum; i++) {
            dx += Math.pow(1.0 * (mdCount[i] - mean), 2);
        }
        dx = dx / mdsNum * 1.0;
        dx = Math.sqrt(dx);
        logger.info(bucketSize + "\t\t" + dx + " " + mean);

        dx = 0;
        int[] mdCount_hash = new int[mdsNum];
        for (int i = 0, j = 0; i < dirCount; i++) {
            mdCount_hash[j] = mdCount_hash[j] + 1;
            if (++j > mdsNum - 1) {
                j = 0;
            }
        }

        for (int i = 0; i < mdsNum; i++) {
            dx += Math.pow(1.0 * (mdCount_hash[i]*dirSize - mean), 2);
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
