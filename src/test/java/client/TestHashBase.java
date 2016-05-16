package client;

import client.perform.BaseMultiMdTest;
import client.service.ClientService;
import client.service.impl.HashBaseImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestHashBase extends BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("TestHashBase");

    private ClientService service = new HashBaseImpl();
    String dirName = "hash_dir";
    String fileName = "hash_file";
    int count = 10;

    @Test
    public void testList() throws RemoteException {
        logger.info("list dir /");
        printMdList(service.listDir("/"));
    }

    @Test
    public void testCreateFileAndDir() throws RemoteException {
        logger.info("begin create dir");
        for (int i = 0; i < count; i++) {
            service.createDirMd("/", dirName + i, getMdAttr(dirName + i, i, true));
        }
        logger.info("list dir /");
        printMdList(service.listDir("/"));

        logger.info("begin create file");
        for (int i = 0; i < count; i++) {
            service.createFileMd("/" + dirName + i, fileName + i, getMdAttr(fileName + i, i, false));
        }
        for (int i = 0; i < count; i++) {
            logger.info("list dir" + "/" + dirName + i);
            printMdList(service.listDir("/" + dirName + i));
        }

    }

    @Test
    public void testRenameDir() throws RemoteException {
        testCreateFileAndDir();
        logger.info("重命名目录后列表目录");
        for (int i = 0; i < count; i++) {
            service.renameDir("/", dirName + i, dirName + i + "_r");
        }
        logger.info("重命名目录后/目录下");
        printMdList(service.listDir("/"));
        logger.info("重命名目录后，新目录名下遍历目录");
        for (int i = 0; i < count; i++) {
            printMdList(service.listDir("/" + dirName + i + "_r"));
        }

    }

    @Test
    public void testDelDir() throws RemoteException {
        testRenameDir();
        logger.info("删除后列表目录");
        for (int i = 0; i < count; i++) {
            service.deleteDir("/", "");
        }
        logger.info("list dir / ");
        printMdList(service.listDir("/"));
        logger.info("list 重命名目录后");
        for (int i = 0; i < count; i++) {
            printMdList(service.listDir("/" + dirName + i + "_r"));
        }
    }

}
