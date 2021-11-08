package com.ziroom.qa.quality.defende.provider.util.xmind;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.util.FileUtil;
import com.ziroom.qa.quality.defende.provider.util.xmind.pojo.Attached;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.ArchiveException;
import org.dom4j.DocumentException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class XmindParser {
    public static final String xmindZenJson = "content.json";
    public static final String xmindLegacyContent = "content.xml";
    public static final String xmindLegacyComments = "comments.xml";

    /**
     * 解析脑图文件，返回Xmind Content整合后的内容
     * @param inputStream
     * @return
     * @throws IOException
     * @throws ArchiveException
     * @throws DocumentException
     * @throws JSONException
     */
    public static Attached getXmindRootBean(InputStream inputStream) throws IOException, ArchiveException, DocumentException, JSONException {
        //1. 将文件解压缩
        String destFileName = ZipUtils.extract(inputStream);

        //2. 获取文件内容
        String content;
        if (isNewVersionXmindZen(destFileName)) {
            content = getXmindZenContent(destFileName);
        } else {
            content = getXmindLegacyContent(destFileName);
        }

        //3. 删除临时解压的文件夹
        File dir = new File(destFileName);
        FileUtil.deleteDir(dir);

        //4. 获取根节点
        Attached xmindRoot = JSON.parseObject(content, Attached.class);
        xmindRoot.getRootTopic().setRoot(true);
        Attached rootAttached = xmindRoot.getRootTopic();

        //5. 为每个孩子节点设置父节点
        setttingXmindTree(rootAttached);
        return rootAttached;
    }

    /**
     * 设置树形结构
     * @param xmindRoot
     */
    private static void setttingXmindTree(Attached xmindRoot) {
        Optional.ofNullable(xmindRoot)
            .map(root -> root.getChildren())
            .ifPresent(
                attachedList ->{
                    List<Attached> list = attachedList.getAttached();
                    if(CollectionUtils.isNotEmpty(list)){
                        xmindRoot.setAttachedChildren(list);
                        list.parallelStream().forEach(attached -> {
                            attached.setParent(xmindRoot);
                            if(!Optional.ofNullable(attached.getChildren()).isPresent()){
                                attached.setLeaf(true);
                            }
                            setttingXmindTree(attached);
                        });
                    }
                });
    }

//    /**
//     * 设置树形结构
//     * @param xmindRoot
//     */
//    private static void setttingXmindTree1(Attached xmindRoot) {
//        if(Objects.isNull(xmindRoot.getChildren())){
//            return;
//        }
//        List<Attached> list = xmindRoot.getChildren().getAttached();
//        xmindRoot.setAttachedChildren(list);
//        if(CollectionUtils.isEmpty(list)){
//            return;
//        }
//        for(Attached attached:list){
//            attached.setParent(xmindRoot);
//            if(!Optional.ofNullable(attached.getChildren()).isPresent()){
//                attached.setLeaf(true);
//            }
//            setttingXmindTree1(attached);
//        }
//    }

    public static String getXmindZenContent(String destFileName) throws IOException {
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(xmindZenJson);
        Map<String, String> map = ZipUtils.getContents(fileNameList, destFileName);
        String content = map.get(xmindZenJson);
        content = XmindZen.getContent(content);
        return content;
    }


    public static String getXmindLegacyContent(String destFileName) throws IOException, DocumentException, JSONException {
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(xmindLegacyContent);
        fileNameList.add(xmindLegacyComments);
        Map<String, String> map = ZipUtils.getContents(fileNameList, destFileName);

        String contentXml = map.get(xmindLegacyContent);
        String commentsXml = map.get(xmindLegacyComments);
        return XmindLegacy.getContent(contentXml, commentsXml);

    }


    /**
     * 判断是否为最新版本的Xmind
     * @param destFileName
     * @return
     */
    private static boolean isNewVersionXmindZen(String destFileName){
        //解压
        File parent = new File(destFileName);
        if (parent.isDirectory()) {
            String[] files = parent.list(new ZipUtils.FileFilter());
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                if (files[i].equals(xmindZenJson)) {
                    return true;
                }
            }
        }
        return false;
    }

}
