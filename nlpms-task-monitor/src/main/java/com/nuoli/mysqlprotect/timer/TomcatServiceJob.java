package com.nuoli.mysqlprotect.timer;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Caiyuhui
 * @date 2020/1/6
 * @time 11:46
 */
@Component
@EnableScheduling
public class TomcatServiceJob {
    Logger logger = Logger.getLogger(TomcatServiceJob.class);
    @Value("${com.nannar.tomcat.name}")
    private String tomcatNames;
    final Runtime runtime = Runtime.getRuntime();
    private static boolean flag = false;
    @Scheduled(cron = "0 0/1 * * * ?")
    public void tomcatMonitorJob() throws Exception {
       if(!flag){
           flag = true;
           String[] tomcatName = tomcatNames.split(",");
           List tomcatNames = new ArrayList();
           for(int i=0;i<tomcatName.length; i++){
               tomcatNames.add(tomcatName[i]);
           }
           Process netStart = runtime.exec("net start");
           InputStream inputStream = netStart.getInputStream();
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
           String info = null;
           List list = new ArrayList();
           while ((info = bufferedReader.readLine())!=null){
               if(info.trim().contains("Apache Tomcat")){
                   list.add(info.trim());
               }
           }
           if(tomcatNames.size()!=list.size()){
               for(int i=0;i<tomcatNames.size();i++){
                   for(int j=0;j<list.size();j++){
                       if(tomcatNames.get(i).equals(list.get(j))){
                           tomcatNames.remove(i);
                           for (int x=0;x<tomcatNames.size();x++){
                               String tomcatServiceName = (String)tomcatNames.get(x);
                               tomcatServiceName = tomcatServiceName.replace("Apache Tomcat 8.5","");
                               tomcatServiceName = tomcatServiceName.trim();
                               Runtime runtime = Runtime.getRuntime();
                               runtime.exec("net start " + tomcatServiceName);
                               logger.info("缺少服务:"+tomcatServiceName);
                           }
                       }
                   }
               }
           }
           if(list.size()==0){
               for(int i=0;i<tomcatNames.size();i++){
                   String tomcatServiceName = (String)tomcatNames.get(i);
                   tomcatServiceName = tomcatServiceName.replace("Apache Tomcat 8.5","");
                   tomcatServiceName = tomcatServiceName.trim();
                   Runtime runtime = Runtime.getRuntime();
                   runtime.exec("net start " + tomcatServiceName);
                   logger.info("找不到服务，重新启动服务:"+tomcatServiceName);
               }
           }
        flag = false;
        return;
       }
    }
}
