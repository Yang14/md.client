package client.perform;

import base.md.MdAttr;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-27.
 */
public class BaseMultiMdTest {
    private static Logger logger = LoggerFactory.getLogger("BaseMultiMdTest");

    public ClientService clientService = new ClientServiceImpl();

    public int threadCount;
    public int count;
    public CountDownLatch latchDir = new CountDownLatch(threadCount);
    public CountDownLatch latchFile = new CountDownLatch(threadCount);

    public CountDownLatch latchForOps = new CountDownLatch(1);

    public String[] threadNameArray;

    public BaseMultiMdTest(int threadCount) {
        this.threadCount = threadCount;
        this.count = 100000 / threadCount;
    }

    public void setUp() throws RemoteException {
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            name[i] = threadName;
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
            name += mdAttr.getName() + ",";
            if (i++ % 100 == 0) {
                logger.info(name);
                name = "";
            }
        }
    }

}
