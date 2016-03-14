package client.facade;

import client.facade.ops.ClientMultiCreate;
import client.facade.ops.ClientMultiFind;
import client.facade.ops.ClientMultiRename;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class SystemPerformance {
    private static Logger logger = LoggerFactory.getLogger("SystemPerformance");
    public static int threadCount;
    public static int count;

    public static void testPerformance(int[] countArray) throws RemoteException, InterruptedException {
        for (int i = 0; i < countArray.length; i++) {
            threadCount = countArray[i];
            switch (threadCount){
                case 1:
                case 2:
                case 4:count = 10000;break;
                case 8:count = 100000/8;break;
                case 16:count = 100000;break;
                case 32:count = 50000;break;
                case 64:count = 20000;break;

            }
            testWithThreadCount(threadCount);
        }
    }

    private static void testWithThreadCount(int count) throws RemoteException, InterruptedException {
//        logger.info(String.format("--------------begin test with %s-----------------", count));
        new ClientMultiCreate().testMultiCreate();
        new ClientMultiFind().testMultiFind();
        new ClientMultiRename().testMultiRename();
        //new ClientMultiDel().testMultiDel();
//        logger.info(String.format("--------------end test -----------------"));
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {
        if (args == null){
            logger.info("need params : thread count.");
            return;
        }
        int[] countArray = new int[args.length];
        int i=0;
        String inputParams = "";
        for (String count : args){
            inputParams += count +" ";
            countArray[i++] = Integer.parseInt(count);
        }
        logger.info("input params:" + inputParams);
        testPerformance(countArray);
    }

}
