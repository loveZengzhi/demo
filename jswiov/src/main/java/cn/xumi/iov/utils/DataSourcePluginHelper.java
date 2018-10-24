package cn.xumi.iov.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.jfinal.config.Plugins;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 从config.properties文件中取多数据源配置
 * Created by DuLerWeil on 2016/6/1.
 */
public final class DataSourcePluginHelper {
    private DataSourcePluginHelper() {}

    public static void config(Plugins me) {
        String[] dsArray = PropKit.get("db.ds", "mysql").split(",");

        for (String ds: dsArray) {
            ds = ds.trim();
            if (StrKit.isBlank(ds)) {
                continue;
            }

            boolean active = PropKit.getBoolean(String.format("db.%s.active", ds), false);
            if (!active) {
                continue;
            }

            String url = PropKit.get(String.format("db.%s.url", ds));
            String username = PropKit.get(String.format("db.%s.username", ds));
            String password = PropKit.get(String.format("db.%s.password", ds));

            DruidPlugin dp = new DruidPlugin(url, username, password)
                .setInitialSize(PropKit.getInt(String.format("db.%s.InitialSize", ds), 10))
                .setMinIdle(PropKit.getInt(String.format("db.%s.MinIdle", ds), 10))
                .setMaxActive(PropKit.getInt(String.format("db.%s.MaxActive", ds), 100))
                .setMaxWait(PropKit.getInt(String.format("db.%s.MaxWait", ds), DruidDataSource.DEFAULT_MAX_WAIT))
                .setTimeBetweenEvictionRunsMillis(PropKit.getLong(String.format("db.%s.TimeBetweenEvictionRunsMillis", ds), DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS))
                .setMinEvictableIdleTimeMillis(PropKit.getLong(String.format("db.%s.MinEvictableIdleTimeMillis", ds), DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS))
                .setValidationQuery(PropKit.get(String.format("db.%s.ValidationQuery", ds), "select 1"))
                .setTestWhileIdle(PropKit.getBoolean(String.format("db.%s.TestWhileIdle", ds), true))
                .setTestOnBorrow(PropKit.getBoolean(String.format("db.%s.TestOnBorrow", ds), false))
                .setTestOnReturn(PropKit.getBoolean(String.format("db.%s.TestOnReturn", ds), false))
                .setMaxPoolPreparedStatementPerConnectionSize(PropKit.getInt(String.format("db.%s.MaxPoolPreparedStatementPerConnectionSize", ds), -1))
                .setFilters(PropKit.get(String.format("db.%s.Filters", ds)));

            dp.setTimeBetweenConnectErrorMillis(PropKit.getLong(String.format("db.%s.TimeBetweenConnectErrorMillis", ds), DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS));
            dp.setRemoveAbandoned(PropKit.getBoolean(String.format("db.%s.RemoveAbandoned", ds), false));
            dp.setRemoveAbandonedTimeoutMillis(PropKit.getLong(String.format("db.%s.RemoveAbandonedTimeoutMillis", ds), 300 * 1000L));
            dp.setLogAbandoned(PropKit.getBoolean(String.format("db.%s.LogAbandoned", ds), false));

            String driver = PropKit.get(String.format("db.%s.driver", ds));
            if (StrKit.notBlank(driver)) {
                dp.setDriverClass(driver);
            }

            ActiveRecordPlugin arp = new ActiveRecordPlugin(ds, dp);

            arp.setShowSql(PropKit.getBoolean("db.show.sql"));
          /*  if (PropKit.getBoolean(String.format("db.%s.default", ds), false)) {
                _MappingKit.mapping(arp);
            }*/
            arp.setDialect(new OracleDialect());
            arp.setContainerFactory(new CaseInsensitiveContainerFactory());//忽略大小写

            me.add(dp);
            me.add(arp);
        }
    }
}
