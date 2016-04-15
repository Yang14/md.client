package client.service.impl;

import base.api.IndexOpsService;
import base.md.MdAttr;
import base.md.MdPos;
import client.service.ClientService;
import client.service.dao.SSDBDao;
import client.service.dao.SSDBDaoImpl;
import client.service.tool.ConnTool;
import client.service.tool.MdPosCacheTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientServiceImpl implements ClientService {
    private static Logger logger = LoggerFactory.getLogger("ClientServiceImpl");
    private static IndexOpsService indexOps = ConnTool.getIndexOpsService();
    private static ThreadLocal<IndexOpsService> indexOpsHolder = new ThreadLocal<IndexOpsService>(){
        public IndexOpsService initialValue(){
            return ConnTool.getIndexOpsService();
        }
    };

    private static SSDBDao ssdbService = new SSDBDaoImpl();

    @Override
    public boolean createFileMd(String parentDirPath, String fileName, MdAttr mdAttr) throws RemoteException {
        MdPos mdPos = getMdPosListByPathForCreateFile(parentDirPath);
        return ssdbService.insertMd(parentDirPath, mdPos, fileName, mdAttr);
    }

    @Override
    public boolean createDirMd(String parentDirPath, String dirName, MdAttr mdAttr) throws RemoteException {
        MdPos mdPos = indexOpsHolder.get().createDirIndex(parentDirPath, dirName);
        if (mdPos == null) {
            logger.error("create dir error: parentDirPath:" + parentDirPath + ",dirName:" + dirName);
            return false;
        }
        return ssdbService.insertMd(parentDirPath,mdPos, dirName, mdAttr);
    }

    @Override
    public MdAttr findFileMd(String parentDirPath, String fileName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        MdAttr mdAttr = null;
        for (MdPos mdPos : mdPosList) {
            mdAttr = ssdbService.findFileMd(mdPos, fileName);
            if (mdAttr != null) {
                break;
            }
        }
        return mdAttr;
    }

    @Override
    public List<MdAttr> listDir(String dirPath) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(dirPath);
        List<MdAttr> mdAttrList = new ArrayList<MdAttr>();
        for (MdPos mdPos : mdPosList) {
            List<MdAttr> partMdAttrList = ssdbService.listDir(mdPos);
            if (partMdAttrList.size() > 0) {
                mdAttrList.addAll(partMdAttrList);
            }
        }
        return mdAttrList;
    }

    @Override
    public boolean renameDir(String parentDirPath, String oldName, String newName) throws RemoteException {
        String separator = parentDirPath.equals("/") ? "" : "/";
        MdPosCacheTool.removeMdPosList(parentDirPath + separator + oldName);
        return indexOpsHolder.get().renameDirIndex(parentDirPath, oldName, newName);
    }

    @Override
    public boolean renameFile(String parentDirPath, String oldName, String newName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        boolean renameResult = false;
        for (MdPos mdPos : mdPosList) {
            renameResult = ssdbService.renameMd(mdPos, oldName, newName);
            if (renameResult) {
                break;
            }
        }
        return renameResult;
    }

    @Override
    public boolean deleteDir(String parentPath, String dirName) throws RemoteException {
        MdPosCacheTool.removeMdPosList(parentPath);
        List<MdPos> mdPosList = getMdPosListByPath(parentPath);
        boolean deleteResult;
        for (MdPos mdPos : mdPosList) {
            deleteResult = ssdbService.deleteMd(mdPos, dirName);
            if (deleteResult) {
                break;
            }
        }
        String separator = parentPath.equals("/") ? "" : "/";
        return indexOpsHolder.get().deleteDir(parentPath + separator + dirName);
    }

    @Override
    public boolean deleteFile(String parentDirPath, String fileName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        boolean renameResult = false;
        for (MdPos mdPos : mdPosList) {
            renameResult = ssdbService.deleteMd(mdPos, fileName);
            if (renameResult) {
                break;
            }
        }
        return renameResult;
    }
    private MdPos getMdPosListByPathForCreateFile(String path) throws RemoteException {
        MdPos mdPos = MdPosCacheTool.getMdPosListFromCacheForCreateFile(path);
        if (mdPos == null) {
            mdPos = indexOpsHolder.get().getMdPosListForCreateFile(path);
            if (mdPos == null) {
                logger.error("getMdPosListByPathForCreateFile for " + path + " error. MdPos == null");
                return null;
            }
            MdPosCacheTool.setMdPosListToCacheForCreateFile(path, mdPos);
        }
        return mdPos;
    }
    private List<MdPos> getMdPosListByPath(String path) throws RemoteException {
        List<MdPos> mdPosList = MdPosCacheTool.getMdPosListFromCache(path);
        if (mdPosList == null) {
            mdPosList = indexOpsHolder.get().getMdPosList(path);
            if (mdPosList == null) {
                return new ArrayList<MdPos>();
            }
            MdPosCacheTool.setMdPosListToCache(path, mdPosList);
        }
        return mdPosList;
    }

}
