package client.facade.ops;

import base.md.MdAttr;
import client.facade.SystemPerformance;
import client.service.ClientService;
import client.service.impl.HashBaseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-27.
 */
public class BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("BaseMultiMdTest");

    public ClientService clientService = new HashBaseImpl();

    public int threadCount = SystemPerformance.threadCount;
    public int count = SystemPerformance.count;
    public int dirI = SystemPerformance.dirI;
    public int dirJ = SystemPerformance.dirJ;
    public int fileI = SystemPerformance.fileI;
    public int fileJ = SystemPerformance.fileJ;
    public CountDownLatch latchDir = new CountDownLatch(threadCount);
    public CountDownLatch latchFile = new CountDownLatch(threadCount);
    public CountDownLatch latchPer = new CountDownLatch(threadCount);
    public DecimalFormat df = new DecimalFormat("0");

    public CountDownLatch latchForOps = new CountDownLatch(1);

    public String[] threadNameArray;


    public void setUp() throws RemoteException {
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i + threadCount;
            name[i] = threadName;
            clientService.createDirMd("/", threadName, getMdAttr(threadName, 5, true));
            clientService.createDirMd("/", "f" + threadName, getMdAttr("f" + threadName, 5, true));
            clientService.createDirMd("/", "r" + threadName, getMdAttr("r" + threadName, 5, true));
            clientService.createDirMd("/", "d" + threadName, getMdAttr("d" + threadName, 5, true));
            clientService.createDirMd("/", threadName + "-forFile", getMdAttr(threadName + "-forFile", 99, true));
            clientService.createDirMd("/", "f" + threadName + "-forFile", getMdAttr("f" + threadName + "-forFile", 99, true));
            clientService.createDirMd("/", "r" + threadName + "-forFile", getMdAttr("r" + threadName + "-forFile", 99, true));
            clientService.createDirMd("/", "d" + threadName + "-forFile", getMdAttr("d" + threadName + "-forFile", 99, true));
        }
        threadNameArray = name;
    }

    public MdAttr getMdAttr(String name, int size, boolean isDir) {
        MdAttr mdAttr = new MdAttr();
        mdAttr.setName(name);
        mdAttr.setSize(size);
        mdAttr.setType(isDir);
        mdAttr.setCreateTime(System.currentTimeMillis());
        return mdAttr;
    }

    public void printMdList(List<MdAttr> mdAttrs) {
        int i = 1;
        String name = "";
        for (MdAttr mdAttr : mdAttrs) {
            if (mdAttr.getType()) {
                name += "[" + mdAttr.getName() + "],";
            } else {
                name += mdAttr.getName() + ",";
            }
            if (i++ % 100 == 0) {
                logger.info(name);
                name = "";
            }
        }
        logger.info(name);
    }

}
