package client.service.impl;

import base.md.MdAttr;
import base.md.MdPos;
import base.tool.PortEnum;
import client.service.ClientService;
import client.service.tool.ConnTool;
import com.alibaba.fastjson.JSON;
import com.sangupta.murmur.Murmur2;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class HashBaseImpl implements ClientService {
    private static Logger logger = LoggerFactory.getLogger("HashBaseImpl");
    private static String[] IPList = new String[]{"192.168.0.58", "192.168.0.60", "192.168.0.61"};
    private static final int SEED = 0xB0F57EE3;

    @Override
    public boolean createFileMd(String parentDirPath, String fileName, MdAttr mdAttr) throws RemoteException {
        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(parentDirPath)));
        return ssdb.hset(calHashCode(parentDirPath), fileName, JSON.toJSONString(mdAttr)).ok();
    }

    @Override
    public boolean createDirMd(String parentDirPath, String dirName, MdAttr mdAttr) throws RemoteException {
        return createFileMd(parentDirPath, dirName, mdAttr);
    }

    @Override
    //todo
    public MdAttr findFileMd(String parentDirPath, String fileName) throws RemoteException {
        return null;
    }

    @Override
    public List<MdAttr> listDir(String dirPath) throws RemoteException {

        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(dirPath)));
        Map<String, String> mdAttrMap = ssdb.hgetall(calHashCode(dirPath)).mapString();
        List<MdAttr> mdAttrs = new ArrayList<MdAttr>();
        for (String value : mdAttrMap.values()) {
            mdAttrs.add(JSON.parseObject(value, MdAttr.class));
        }
        return mdAttrs;
    }

    /**
     * 1.重命名dir父目录中名称
     * 2.迁移dir完整路径对应桶中数据
     */
    @Override
    public boolean renameDir(String parentDirPath, String oldName, String newName) throws RemoteException {
        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(parentDirPath)));
        MdAttr mdAttr = JSON.parseObject(ssdb.hget(calHashCode(parentDirPath), oldName).asString(), MdAttr.class);
        mdAttr.setName(newName);
        ssdb.hset(calHashCode(parentDirPath), newName, JSON.toJSONString(mdAttr));
        ssdb.hdel(calHashCode(parentDirPath), oldName);

        String fullDirPath = parentDirPath.equals("/") ?
                parentDirPath + oldName : parentDirPath + "/" + oldName;
        ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(fullDirPath)));
        Map<String, String> fileMap = ssdb.hgetall(calHashCode(fullDirPath)).mapString();
        ssdb.hclear(oldName);

        fullDirPath = parentDirPath.equals("/") ?
                parentDirPath + newName : parentDirPath + "/" + newName;
        ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(fullDirPath)));
        for (String key : fileMap.keySet()) {
            ssdb.hset(calHashCode(fullDirPath), key, fileMap.get(key));
        }
        return true;
    }

    @Override
    //todo
    public boolean renameFile(String parentDirPath, String oldName, String newName) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteDir(String parentPath, String dirName) throws RemoteException {
        String fullDirPath = parentPath.equals("/") ?
                parentPath + dirName : parentPath + "/" + dirName;
        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(fullDirPath)));
        Map<String, String> fileMap = ssdb.hgetall(calHashCode(fullDirPath)).mapString();
        ssdb.hclear(calHashCode(fullDirPath));

        for (String value : fileMap.values()) {
            MdAttr mdAttr = JSON.parseObject(value, MdAttr.class);
            if (mdAttr.getType()) {
                deleteDir(fullDirPath, mdAttr.getName());
            }
        }
        return true;
    }

    @Override
    //todo
    public boolean deleteFile(String parentDirPath, String fileName) throws RemoteException {
        return false;
    }

    private MdPos genMdPos(String ip) {
        return new MdPos(ip, PortEnum.SSDB_PORT, 0L);
    }

    private String getIpByPath(String path) {
        byte[] bytes = path.getBytes();
        return IPList[((int) (Murmur2.hash(bytes, bytes.length, SEED) % IPList.length))];
    }

    private String calHashCode(String key) {
        byte[] bytes = key.getBytes();
        return String.valueOf(Murmur2.hash(bytes, bytes.length, SEED));
    }
}
