package cn.xumi.iov;


import cn.xumi.iov.utils.DataSourcePluginHelper;
import com.jfinal.config.Plugins;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created by xumi on 2018/9/10.
 */
public class Launcher {
    private static final Logger logger= LogManager.getLogger(Launcher.class);

    public static void main(String [] args){
    String path=System.getProperty("global.configurationFile");
    if(StrKit.isBlank(path)){
        PropKit.use("config.properties");
    }else{
        PropKit.use(new File(path));
    }
        Plugins plugins = new Plugins();
        //启动Oracle
        DataSourcePluginHelper.config(plugins);

        //启动插件
        List<IPlugin> pluginList = plugins.getPluginList();
        if (pluginList!=null){
            for (IPlugin plugin : pluginList){
                plugin.start();
            }
        }
        logger.info("统一集成模块启动成功...");
        SimOrderPkg simOrderPkg=new SimOrderPkg();
        simOrderPkg.getMsisdn();
    }



}
