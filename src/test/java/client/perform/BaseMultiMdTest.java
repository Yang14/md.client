package client.perform;

import client.service.ClientService;
import client.service.impl.ClientServiceImpl;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-27.
 */
public class BaseMultiMdTest {

    public ClientService clientService = new ClientServiceImpl();

    public int threadCount = 16;
    public int count = 10000;
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

}
