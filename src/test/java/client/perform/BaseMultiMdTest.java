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

    public int threadCount = 15;
    public int count = 100000 ;
    public CountDownLatch latchDir = new CountDownLatch(threadCount);
    public CountDownLatch latchFile = new CountDownLatch(threadCount);

    public CountDownLatch latchForOps = new CountDownLatch(1);

    public String[] threadNameArray;

    public void setUp() throws RemoteException {
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            name[i] = threadName;
        }
        threadNameArray = name;
    }

    public void printMdList(List<MdAttr> mdAttrs) {
        int i = 0;
        String name = "";
        for (MdAttr mdAttr : mdAttrs) {
            name += mdAttr.getName() + ",";
            if (i++ % 50 == 0) {
                logger.info(name);
                name = "";
            }
        }
    }

}
