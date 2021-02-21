package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * 实现FastDFS文件管理
 *  文件上传
 *  文件删除
 *  文件下载
 *  文件信息获取
 *  Storage信息获取
 *  Tracker信息获取
 */
public class FastDFSUtil {

    /**
     * 加载Tracker连接信息
     */
    static {
        try {
            String filename = new ClassPathResource("fdfs_client.conf").getPath();
            //加载Tracker连接信息
            ClientGlobal.init(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Tracker信息
     *
     * @return
     */
    public static String getTrackerInfo() throws Exception {
        //创建一个Tracker的文件访问客户端TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        int tracker_http_port = ClientGlobal.getG_tracker_http_port();//8080
        String ip = trackerServer.getInetSocketAddress().getHostString();
        String url = "http://" + ip + ":" + tracker_http_port;
        return url;
    }

    /**
     * 获取Storage的ip和端口信息
     *
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws Exception {
        //创建一个Tracker的文件访问客户端TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取Storage的ip和端口信息
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    /**
     * 获取storage信息
     *
     * @return
     */
    public static StorageServer getStorages() throws Exception {
        //创建一个Tracker的文件访问客户端TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 文件上传
     *
     * @param fastDFSFile：上传的文件信息封装
     */
    public static String[] upload(FastDFSFile fastDFSFile) throws IOException, MyException {
        //创建一个Tracker的文件访问客户端TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer的连接信息可以获取Storage的连接信息，创建StorageClient连接对象存储Storage连接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        /**
         *   通过StorageClient访问Storage，实现文件上传，并且获取文件上传后的存储信息
         *   第一个参数：上传文件的字节数组
         *   第二个参数：文件的扩展名
         *   第三个参数：附加信息   比如：拍摄地址。。。
         *   函数返回的String[]参数：
         *      String[0]：文件上传所存储的Storage的组名字 group1
         *      String[1]：文件存储到Storage上的文件名字   /M00/02/44/wqdsad.jpg
         */
        String[] uploads = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), null);
        return uploads;
    }

    /**
     * 获取文件信息
     *
     * @Param groupName：文件的组名
     * @Param remoteFileName：文件的存储路径名字
     */
    public static FileInfo getFile(String groupName, String remoteFileName) throws IOException, MyException {
        //创建TrackerClient对象，通过TrackerClient访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的连接对象
        TrackerServer connection = trackerClient.getConnection();
        //创建StorageClient对象存储Storage信息
        StorageClient storageClient = new StorageClient(connection, null);
        //获取文件信息
        return storageClient.get_file_info(groupName, remoteFileName);
    }

    /**
     * 删除文件
     *
     * @param groupName
     * @param remoteFileName
     * @throws IOException
     * @throws MyException
     */
    public static void deleteFile(String groupName, String remoteFileName) throws IOException, MyException {
        //创建TrackerClient对象，通过TrackerClient访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的连接对象
        TrackerServer connection = trackerClient.getConnection();
        //创建StorageClient对象存储Storage信息
        StorageClient storageClient = new StorageClient(connection, null);
        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 文件下载
     *
     * @Param groupName：文件的组名
     * @Param remoteFileName：文件的存储路径名字
     */
    public static InputStream downLoad(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        StorageClient storageClient = new StorageClient(connection, null);
        byte[] bytes = storageClient.download_file(groupName, remoteFileName);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return byteArrayInputStream;
    }

    public static void main(String[] args) throws Exception {
//        FileInfo group1 = getFile("group1", "M00/00/00/rBEnFGARtciAWSLDAABXEEJ5gW4241.jpg");
//        System.out.println(group1.getSourceIpAddr());
//        System.out.println(group1.getFileSize());
//        System.out.println(group1.getCreateTimestamp());
//        System.out.println(group1.getCrc32());
        //    InputStream group1 = downLoad("group1", "M00/00/00/rBEnFGARtciAWSLDAABXEEJ5gW4241.jpg");
        //    FileOutputStream fileOutputStream = new FileOutputStream("F://1.jpg");
        //    //定义一个缓冲区
        //    byte[] bytes = new byte[1024];
        //    while (group1.read(bytes) != -1) {
        //        fileOutputStream.write(bytes);
        //    }
        //    fileOutputStream.flush();
        //    group1.close();
        //    fileOutputStream.close();
        /**
         * 文件删除
         */
        //deleteFile("group1", "M00/00/00/rBEnFGARxSOAR0XgAABorQhpGjU035.jpg");
        //StorageServer storages = getStorages();
        //System.out.println(storages.getStorePathIndex());
        //System.out.println(storages.getInetSocketAddress().getHostString());
        //ServerInfo[] group1s = getServerInfo("group1", "M00/00/00/rBEnFGARyYyAShPGAABorQhpGjU129.jpg");
        //for(ServerInfo a:group1s){
        //    System.out.println(a.getIpAddr());
        //    System.out.println(a.getPort());
        //}
//        System.out.println(getTrackerInfo());
    }
}
