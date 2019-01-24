package com.telegram.hook.utils;








import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataCleanManager {
    private static List<String> dataList = new ArrayList<>();

    static {
//        dataList.add("/data/user/0/org.telegram.messenger/files/dc1conf.dat");
//        dataList.add("/data/user/0/org.telegram.messenger/files/account2");
//        dataList.add("/data/user/0/org.telegram.messenger/files/cache4.db");
//        dataList.add("/data/user/0/org.telegram.messenger/files/cache4.db-wal");
//        dataList.add("/data/user/0/org.telegram.messenger/files/dc2conf.dat");
//        dataList.add("/data/user/0/org.telegram.messenger/files/stats2.dat");
//        dataList.add("/data/user/0/org.telegram.messenger/files/remote_en.xml");
//        dataList.add("/data/user/0/org.telegram.messenger/files/tgnet.dat");
//        dataList.add("/data/user/0/org.telegram.messenger/files/persisted_config");
//        dataList.add("/data/user/0/org.telegram.messenger/files/cache4.db-shm");
//        dataList.add("/data/user/0/org.telegram.messenger/files/account1");
//        dataList.add("/data/user/0/org.telegram.messenger/files/dc5conf.dat");

        dataList.add("/data/user/0/org.telegram.messenger/files/cache4.db");
    }

    public static void deleData() {
//        File file;
//        for (int i = 0; i < dataList.size(); i++) {
//            file = new File(dataList.get(i));
//            if (file.exists()) {
//             Boolean isok=   file.delete();
//            }
//        }
//      String parentPath=  App.getContext().getFilesDir().getParentFile().getAbsolutePath();

        String parentPath="/data/user/0/org.telegram.messenger";


//        deleteDirectory(new File("/data/user/0/org.telegram.messenger.beta/cache"));
//        deleteDirectory(new File("/data/user/0/org.telegram.messenger.beta/files"));
//        deleteDirectory(new File("/data/user/0/org.telegram.messenger.beta/lib"));
//        deleteDirectory(new File("/data/user/0/org.telegram.messenger.beta/no_backup"));
//        deleteDirectory(new File("/data/user/0/org.telegram.messenger.beta/shared_prefs"));


        deleteDirectory(new File(parentPath+"/files"));
        deleteDirectory(new File(parentPath+"/no_backup"));
        deleteDirectory(new File(parentPath+"/shared_prefs"));
    }

    /**
     * 删除目录及目录下的文件
     * <p>
     * <p>
     * 要删除的目录的文件路径
     *
     * @return 目录删除成功返回true，否则返回false
     */

    public static boolean deleteDirectory(File dirFile) {
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {

//            MyLog.write2File("删除目录失败：    不存在"+dirFile.getAbsolutePath());
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files==null){
            return true;
        }
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]);
                if (!flag)
                    break;
            }
        }
        if (!flag) {

//            MyLog.write2File("删除目录失败");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {

            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {

//                MyLog.writeSwitchAccount("删除单个文件" + fileName + "成功！");
                return true;
            } else {
//                MyLog.writeSwitchAccount("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            MyLog.writeSwitchAccount("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
