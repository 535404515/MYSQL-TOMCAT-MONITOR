package com.nuoli.mysqlprotect.timer;
import com.nuoli.mysqlprotect.util.EncryptUtil;
import com.nuoli.mysqlprotect.util.MysqlUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Caiyuhui
 * @date 2020/1/6
 * @time 8:53
 */
@Component
@EnableScheduling
public class MysqlServiceJob {
    @Value("${com.nannar.mysql.user}")
    private String username;
    @Value("${com.nannar.mysql.pass}")
    private String password;
    private static boolean flag = false;
    private static int switching = 0;
    final Logger logger = Logger.getLogger(MysqlServiceJob.class);
    @Scheduled(cron = "0 0/1 * * * ?")
    public void mysqlMonitorJob() throws Exception {
        if(flag == false){
            flag = true;
            int count = 2;
            if(switching > count){
                MysqlUtil.restartMysql();
                switching = 0;
                logger.info("mysql重启成功");
            }
            String infoMySqlIsOpen = MysqlUtil.getInfoMySqlIsOpen(EncryptUtil.Base64Decode(username), EncryptUtil.Base64Decode(password));
            if("".equals(infoMySqlIsOpen)){
                switching++;
            }
            logger.info("状态:"+switching);
            flag = false;
            return;
        }
    }
}
