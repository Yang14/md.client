package client.service.impl;

import base.api.IndexOpsService;
import base.md.MdAttr;
import base.md.MdPos;
import base.tool.PortEnum;
import client.service.tool.ConnTool;
import com.alibaba.fastjson.JSON;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientServiceImplV2 {
    private static Logger logger = LoggerFactory.getLogger("ClientServiceImplV2");
    private static IndexOpsService indexOps = ConnTool.getIndexOpsService();

    private static String IP1 = "192.168.0.58";
    private static String IP2 = "192.168.0.61";

    private static Map<String, String> pathToIpMap = new HashMap<String, String>();

    private static ThreadLocal<IndexOpsService> indexOpsHolder = new ThreadLocal<IndexOpsService>() {
        public IndexOpsService initialValue() {
            return ConnTool.getIndexOpsService();
        }
    };

    public ClientServiceImplV2() {
        setUp();
    }

    private void setUp() {
        pathToIpMap.put("hashPath", IP1);
        pathToIpMap.put("hashPath2", IP2);
    }

    private MdPos genMdPos(String ip) {
        return new MdPos(ip, PortEnum.SSDB_PORT, 0L);
    }

    private String getIpByPath(String path) {
        return pathToIpMap.get(path) == null ? IP1 : pathToIpMap.get(path);
    }


    private MdAttr getMdAttr(String name, int size, boolean isDir) {
        MdAttr mdAttr = new MdAttr();
        mdAttr.setName(name);
        mdAttr.setSize(size);
        mdAttr.setType(isDir);
        mdAttr.setCreateTime(System.currentTimeMillis());
        return mdAttr;
    }

    public void createFile(String path, int count) {
        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(path)));
        for (int i = 0; i < count; ++i) {
            String fileName = "hashFile";
            ssdb.hset(path, fileName + i, JSON.toJSONString(getMdAttr(fileName + i, i, false)));
        }
    }

    public void renameDir(String oldName, String newName) {
        SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(oldName)));
        Map<String, String> fileMap = ssdb.hgetall(oldName).mapString();
        ssdb.hclear(oldName);

        ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(newName)));
        for (String key : fileMap.keySet()) {
            ssdb.hset(newName, key, fileMap.get(key));
        }
    }

    public void clearHashBase() {
        for (String key : pathToIpMap.keySet()) {
            SSDB ssdb = ConnTool.getSSDB(genMdPos(getIpByPath(key)));
            ssdb.hclear(key);
        }
    }
}
