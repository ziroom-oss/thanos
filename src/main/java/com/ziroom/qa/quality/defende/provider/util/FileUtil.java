package com.ziroom.qa.quality.defende.provider.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtil {

    /**
     * 删除目录
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 获取文件内容
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileContent(String fileName) throws IOException {
        File file;
        FileReader fileReader = null;
        BufferedReader bufferedReder = null;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            file = new File(fileName);
            fileReader = new FileReader(file);
            bufferedReder = new BufferedReader(fileReader);

            while (bufferedReder.ready()) {
                stringBuffer.append(bufferedReder.readLine());
            }
        } catch (Exception e) {
            throw new RuntimeException("找不到该文件");
        } finally {
            //打开的文件需关闭，在unix下可以删除，否则在windows下不能删除（file.delete())
            bufferedReder.close();
            fileReader.close();
        }
        return stringBuffer.toString();
    }
    public static String readFile(String filePath) {
        String logStr = null;
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] b = new byte[is.available()];
            is.read(b);
            logStr = new String(b);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return logStr;
    }

    /**
     * 读取文件，在线文件
     *
     * @param urlPath 在线文件路径
     * @return
     */
    public static StringBuffer getFileContent2(String urlPath) {
        StringBuffer contents = new StringBuffer();

        URL url;
        URLConnection conn;
        try {
            if(urlPath.contains(".t.")||urlPath.contains(".kt.")||urlPath.contains(".q.")||urlPath.contains(".kq.")||urlPath.contains(".uat.")){
                URL urlOld = new URL(urlPath);
                String path = urlOld.getPath();
                if(path.contains(".html")){
                    path = path.replace("doc.html","v2/api-docs")
                            .replace("swagger-ui.html","v2/api-docs");
                }
                urlPath = urlPath.replace(urlOld.getHost(),"10.216.4.19");
                url = new URL(urlPath);
                conn = url.openConnection();
                conn.setRequestProperty("host",urlOld.getHost());
            }else{
                url = new URL(urlPath);
                conn = url.openConnection();
            }
            conn.connect();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    contents.append(line);
                }
            } else {
                log.info("Cannot connect to the " + urlPath);
                throw new RuntimeException("连接swagger地址异常！"+urlPath);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("连接swagger地址异常！"+urlPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("连接swagger地址异常！"+urlPath);
        }

        log.info("Finished getting the contents from URL...");
        return contents;
    }


    /**
     * 创建多层目录
     *
     * @param dirPath 文件夹目录
     */
    public static void mkdir(String dirPath) {
        File myFolderPath = new File(dirPath);
        try {
            if (!myFolderPath.exists() && !myFolderPath.isDirectory()) {
                myFolderPath.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建新文件
     *
     * @param filePath 文件存储路径
     */
    public static void touchFile(String filePath) {
        File myFilePath = new File(filePath);
        try {
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回文件后缀类型
     *
     * @param filePath 文件完整路径，例如：/usr/local/kingdow/test/helloworld.java
     * @return 文件后缀，例如：java
     */
    public static String getFileType(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 返回文件后缀类型
     *
     * @param file 文件，例如：/usr/local/kingdow/test/helloworld.java
     * @return 文件后缀，例如：java
     */
    public static String getFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return null;
    }

    /**
     * 根据文件路径获取文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath.trim());
        return file.getName();
    }

    /**
     * 根据jmx文件路径，替换文件内容中的路径
     *
     * @param jmxFilePath
     * @param csvFilePath
     * @return true 表示成功； false 表示失败；
     * @throws IOException
     */
    public static Boolean replaceParameterPath(String jmxFilePath, String csvFilePath,String JdbcJarPath) throws IOException {
        File oldFile = new File(jmxFilePath);
        if (!oldFile.exists()) {
            log.info("jmx文件不存在");
            return false;
        }
        BufferedReader readBuffer = null;
        File newFile = null;
        BufferedWriter write = null;
        try {
            readBuffer = new BufferedReader(new FileReader(oldFile));
            newFile = new File(jmxFilePath + "01");
            write = new BufferedWriter(new FileWriter(newFile));
            String line = readBuffer.readLine();
            String oldPath;
            String oldJdbcPath;
            String newLine;
            while (line != null) {
                oldPath = oldCsvPath(line);
                oldJdbcPath = oldJdbcPath(line);
                if (null != oldPath && line.contains(oldPath)) {
                    newLine = line.replace(oldPath, csvFilePath);
                    write.write(newLine + "\r\n");
                }else if(null != oldJdbcPath && line.contains(oldJdbcPath)) {
                    newLine = line.replace(oldJdbcPath, JdbcJarPath);
                    write.write(newLine + "\r\n");
                }
                else {
                    write.write(line + "\r\n");
                }
                line = readBuffer.readLine();
            }
        } catch (Exception e) {
            log.error("异常提示：", e);
            throw  new RuntimeException("替换文件内容中的路径失败，error msg："+e.getMessage());
        } finally {
            write.flush();
            write.close();
            readBuffer.close();
        }
        //删除原文件
        boolean isDel = oldFile.delete();
        log.info("文件删除是否成功:" + isDel);
        //新文件重命名
        boolean isRen = newFile.renameTo(oldFile);
        log.info("文件重命名是否成功：" + isRen);
        return isDel && isRen;
    }

    private static final String START = "<stringProp name=\"filename\">";
    private static final String END = "</stringProp>";
    private static final String CSV = ".csv";

    private static String oldCsvPath(String path) {
        if (path.contains(START) && path.contains(CSV)) {
            path = path.substring(path.indexOf(START) + START.length(), path.indexOf(END));
            log.info("需要被替换的csv文件的路径为：" + path);
            return path;
        }
        return null;
    }

    private static final String JDBC_START = "<stringProp name=\"TestPlan.user_define_classpath\">";
    private static final String JDBC_END = "</stringProp>";
    private static final String JDBC_CONNECT = "mysql-connector-java";

    private static String oldJdbcPath(String path){
        if (path.contains(JDBC_START) && path.contains(JDBC_CONNECT)) {
            path = path.substring(path.indexOf(JDBC_START) + JDBC_START.length(), path.indexOf(JDBC_END));
            log.info("需要被替换的JDBC文件的路径为：" + path);
            return path;
        }
        return null;
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static Boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 上传文件方法
     *
     * @param file     上传的文件
     * @return
     */
    public static String analyse(MultipartFile file) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(file.getOriginalFilename()).append("|");
        try  (InputStream inputStream = file.getInputStream()){
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (IOException e) {
                log.error("读取文件内容异常");
                stringBuffer.append("ERROR");
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            log.error("读取文件内容异常");
            stringBuffer.append("ERROR");
        }
        return stringBuffer.toString();
    }
    public static Boolean commonUpload(MultipartFile file, File saveFile) {
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile))) {
            out.write(file.getBytes());
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 文件下载方法
     *
     * @param resp     HttpServletResponse
     * @param filePath 文件路径
     */
    public static void commonDownload(HttpServletResponse resp, String filePath) {
        String fileName = "";
        try {
            fileName = new String(getFileName(filePath).getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = new File(filePath);
        resp.reset();
        resp.setContentType("application/octet-stream");
        resp.setCharacterEncoding("utf-8");
        resp.setContentLength((int) file.length());
        resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件下载
     *
     * @param resp
     * @param filePath
     */
    public static void commonFileDownload(HttpServletResponse resp, String filePath) {
        try {
            File file = new File(filePath);
            // 获取文件名 - 设置字符集
            String downloadFileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso-8859-1");
            // 以流的形式下载文件
            InputStream fis;
            fis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            resp.reset();
            // 设置response的Header
            resp.addHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
            resp.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
            resp.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * 字节流转换成文件
     *
     * @param ins
     * @param file
     */
    public static void inputstreamtofile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件下一层所有文件
     * @param dir 目录
     * @return 文件绝对路径列表
     */
    public static String[] readDirFilesSimply(String dir){
        File directory = new File(dir);
        List<String> list = new ArrayList<>();
        if(directory.exists() && directory.isDirectory()){
            for(File f : directory.listFiles()){
                list.add(f.getAbsolutePath());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 删除递归删除所有文件或者文件夹
     * @param file
     * @return
     */
    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

}
