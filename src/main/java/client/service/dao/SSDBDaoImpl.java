package client.service.dao;

import base.md.MdAttr;
import base.md.MdPos;
import client.service.tool.ConnTool;
import client.service.tool.JedisPoolUtils;
import client.service.tool.MdPosCacheTool;
import com.alibaba.fastjson.JSON;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr-yang on 16-2-23.
 */
public class SSDBDaoImpl implements SSDBDao {
    private static Logger logger = LoggerFactory.getLogger("SSDBDaoImpl");
    private static int MAX_BUCKET_SIZE = 10;

    public boolean insertMd(String parentDirPath, MdPos mdPos, String name, MdAttr mdAttr) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        int count = ssdb.hsize(mdPos.getdCode()).asInt();
        if (count > MAX_BUCKET_SIZE) {
            //clear cache with dCode
            MdPosCacheTool.removeMdPosList(parentDirPath);
            MdPosCacheTool.removeMdPosListForCreateFile(parentDirPath);
            //remind index server, and insert new dCode, using redis pusSub
            Jedis jedis = JedisPoolUtils.getJedis();
            jedis.publish("overSizeDCode", String.valueOf(mdPos.getdCode()));
            JedisPoolUtils.returnResource(jedis);
        }
        Response response = ssdb.hset(mdPos.getdCode(), name, JSON.toJSONString(mdAttr));
        return response.ok();
    }

    public MdAttr findFileMd(MdPos mdPos, String name) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        return JSON.parseObject(ssdb.hget(mdPos.getdCode(), name).asString(), MdAttr.class);
    }

    public List<MdAttr> listDir(MdPos mdPos) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        Map<String, String> mdAttrMap = ssdb.hgetall(mdPos.getdCode()).mapString();
        List<MdAttr> mdAttrs = new ArrayList<MdAttr>();
        for (String value : mdAttrMap.values()) {
            mdAttrs.add(JSON.parseObject(value, MdAttr.class));
        }
        return mdAttrs;
    }

    public boolean renameMd(MdPos mdPos, String oldName, String newName) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        long dCode = mdPos.getdCode();
        MdAttr mdAttr = JSON.parseObject(ssdb.hget(dCode, oldName).asString(), MdAttr.class);
        mdAttr.setName(newName);
        ssdb.hdel(dCode, oldName);
        return ssdb.hset(dCode, newName, JSON.toJSONString(mdAttr)).ok();
    }

    public boolean deleteMd(MdPos mdPos, String name) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        return !ssdb.hdel(mdPos.getdCode(), name).notFound();
    }

    @Override
    public boolean deleteDirMd(MdPos mdPos) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        return ssdb.hclear(mdPos.getdCode()).ok();
    }

}
