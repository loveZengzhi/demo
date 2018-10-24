package cn.xumi.iov;

import cn.xumi.iov.utils.CSVUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by love_ on 2018/9/10.
 */
public class SimOrderPkg {
    private static final Logger log= LogManager.getLogger(SimOrderPkg.class);

    public void getMsisdn(){
        File file=new File(PropKit.get("simOrderPkgPath"));
        List<String []> line= CSVUtils.read(file);
        Map<String,JSONArray> map=new HashMap<>();
        List<JSONObject> recordTemp=new ArrayList<>();
        List<JSONObject> confirm=new ArrayList<>();
        JSONArray lineJson=new JSONArray();
        log.info("line is size {}",line.size());
        String msidn="";
        StringBuilder chkProd=new StringBuilder("");
        int chkConfirm=0;
        int temps=0;
        for (String [] lineStr:line ) {
            if(StrKit.notBlank(lineStr[0])){
                if (StrKit.isBlank(lineStr[5])){
                    continue;
                }
                if(!msidn.equals("") && !msidn.equals(lineStr[0])){
                        for (JSONObject json:confirm
                                ) {
                            lineJson.add(json);
                        }

                        for (JSONObject json:recordTemp
                                ) {
                            lineJson.add(json);
                        }
                    map.put(msidn,lineJson);
                    temps++;
                    chkConfirm=0;
                    confirm.clear();
                    recordTemp.clear();
                    lineJson=new JSONArray();
                    chkProd = new StringBuilder("");

                }

                JSONObject msisdnIds=new JSONObject();
                    msisdnIds.put("ProdID",lineStr[4]);
                    msisdnIds.put("ProdInstID",lineStr[6]);
                    msisdnIds.put("PkgProdInstID",lineStr[5]);
                    msisdnIds.put("pkgProdID",lineStr[7]);

                    if(chkProd.toString().contains(lineStr[4])){
                        chkConfirm++;
                        confirm.add(msisdnIds);
                        if(chkConfirm==1){
                            recordTemp.clear();
                        }
                    }else {
                        recordTemp.add(msisdnIds);
                    }
                    chkProd.append(lineStr[4]);
                    chkProd.append(",");
                msidn=lineStr[0];
            }
        }
       updateSimOrderPkg(map);
    }

    public void updateSimOrderPkg(Map<String,JSONArray> simIds){
        StringBuilder simOrderPkgSql=new StringBuilder("update SIM_ORDER_PKG set prod_inst_ids=? where msisdn=?");
        int temp=0;
        for (String msisdn:simIds.keySet()) {
            temp++;
            //List<ResultSet> result= Db.query("select * from SIM_ORDER_PKG where MSISDN=?","1440081240279");
            Db.update("update sim_order_pkg set prod_inst_ids=? where msisdn=? and del_flag=0",simIds.get(msisdn).toJSONString(),msisdn);
            log.info("第 {} 次：map content is {},{}",temp,simIds.get(msisdn));
        }

    }

}
