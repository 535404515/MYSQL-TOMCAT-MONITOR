package com.nuoli.mysqlprotect.util;
import java.io.IOException;
import java.io.InputStream;
/**
 * @author Caiyuhui
 * @date 2020/1/6
 * @time 9:07
 */
public class MysqlUtil {
    public static String getInfoMySqlIsOpen(String username,String password){
        Process p = null;
        try{
            p = new ProcessBuilder("mysqladmin","-u"+username,"-p"+password,"ping").start();
        }catch(IOException e){
            return "获取mysql是否停止异常";
        }
        byte[] b = new byte[1024];
        int readbytes = -1;
        StringBuffer sb = new StringBuffer();
        InputStream in = p.getInputStream();
        try{
            while((readbytes = in.read(b)) != -1){
                sb.append(new String(b,0,readbytes));
            }
        }catch(IOException e1){
            return "读取流异常";
        }finally {
            try{
                in.close();
            }catch (IOException e2){
                return "关闭流异常";
            }
        }
        return sb.toString();
    }
    public static void restartMysql() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("net stop mysql57");
        runtime.exec("net start mysql57");
    }
}
