package com.ziroom.qa.quality.defende.provider.util.xmind;

import com.ziroom.qa.quality.defende.provider.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ZipUtils {

    private static final String currentPath = System.getProperty("user.dir");

//    private static ZipHomeProperties zipHomeProperties;

    /**
     * 找到压缩文件中匹配的子文件，返回的为
     * getContents("comments.xml,
     * unzip
     *
     * @param subFileNames
     * @param destFilePath
     * @return
     * @throws IOException
     */
    public static Map<String, String> getContents(List<String> subFileNames, String destFilePath) throws IOException {
        Map<String, String> map = new HashMap<>();
        File destFile = new File(destFilePath);
        if (destFile.isDirectory()) {
            String[] res = destFile.list(new FileFilter());
            for (int i = 0; i < Objects.requireNonNull(res).length; i++) {
                if (subFileNames.contains(res[i])) {
                    String s = destFilePath + File.separator + res[i];
                    String content = FileUtil.getFileContent(s);
                    map.put(res[i], content);
                }
            }
        }
        return map;
    }


    /**
     * 返回解压后的文件夹名字
     *
     * @return
     * @throws IOException
     * @throws ArchiveException
     */
    public static String extract(InputStream inputStream) throws IOException, ArchiveException {
        //1. 解压缩之前的源文件
        String sourceName = "/app/logs" + File.separator + "SrcXMind" + System.currentTimeMillis(); //目标文件夹名字
        log.info("源文件目录：", sourceName);
        File sourceFile = new File(sourceName);
        FileUtils.copyInputStreamToFile(inputStream, sourceFile);

        //2. 将源文件压缩至临时目录
        String destFileName = "/app/logs" + File.separator + "XMind" + System.currentTimeMillis(); //目标文件夹名字
        log.info("目标临时目录 destFileName：{}", destFileName);
        Expander expander = new Expander();
        expander.expand(sourceFile, new File(destFileName));

        FileUtils.deleteQuietly(sourceFile);
        return destFileName;
    }

    //这是一个内部类过滤器,策略模式
    static class FileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            //String的 endsWith(String str)方法  筛选出以str结尾的字符串
            if (name.endsWith(".xml") || name.endsWith(".json")) {
                return true;
            }
            return false;
        }
    }


}
