package client;

import base.md.MdAttr;
import client.perform.BaseMultiMdTest;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestRename extends BaseMultiMdTest {
    static Logger logger = LoggerFactory.getLogger("TestRename");
    ClientService clientService = new ClientServiceImpl();

    @Test
    public void testRename() throws RemoteException {
        String path = "/";
        String subPath = "rename";
        clientService.createDirMd(path,subPath, getMdAttr(subPath,9, true));
        buildSubDir(path+subPath);
        renameDir(path+subPath);
    }

    @Test
    public void testDel() throws RemoteException {
        String path = "/";
        String subPath = "del";
        clientService.createDirMd(path,subPath, getMdAttr(subPath,9, true));
        buildSubDir(path+subPath);
        listAll(path+subPath);
        clientService.deleteDir(path+subPath+"/s0","s1");
        logger.info("afert del");
        listAll(path+subPath+"/s0");
        listAll(path+subPath+"/s0/s1/s2");
    }
    private void listAll(String path) throws RemoteException {
        List<MdAttr> mdAttrs = clientService.listDir(path);
        System.out.print(path+"  ");
        printMdList(mdAttrs);
        for (MdAttr mdAttr : mdAttrs){
            if (mdAttr.getType()){
                listAll(path+"/" + mdAttr.getName());
            }
        }
    }
    private void renameDir(String parentDir) throws RemoteException {
        String path = "";
        String temp = "";
        String nName ;
        for (int i = 0; i < 5; i++) {
            temp = "s"+i;
            nName = "r" + temp;
            clientService.renameDir(parentDir + path, temp, nName);
            path+="/"+nName;
            for (int j = 0; j < 5; j++) {
                clientService.renameDir(parentDir + path, "rdir" + j, "r-dir" + j);
            }
            System.out.print(parentDir+path+"  ");
            printMdList(clientService.listDir(parentDir + path));
        }
    }
    private void buildSubDir(String parentDir) throws RemoteException {

        String path = "";
        String temp = "";
        for (int i = 0; i < 5; i++) {
            temp = "s"+i;
            clientService.createDirMd(parentDir+path, temp, getMdAttr(temp,i, true));
            path+="/"+temp;
            for (int j=0;j<5;j++) {
                clientService.createDirMd(parentDir+path, "rdir" + j, getMdAttr("rdir" + j, i, true));
            }
            System.out.print(parentDir+path+"  ");
            printMdList(clientService.listDir(parentDir+path));
        }
    }
}
