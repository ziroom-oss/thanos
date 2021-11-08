package com.ziroom.qa.quality.defende.provider.util;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class XmindUtil {


    /**
     * 获取Xmind根节点
     * @param inputStream
     * @return
     * @throws IOException
     * @throws CoreException
     */
    public static ITopic getRootXmindTopic(InputStream inputStream) throws IOException, CoreException {
        // 初始化builder
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        // 获取workbook
        IWorkbook workbook = builder.loadFromStream(inputStream,new ByteArrayStorage());
        // 获取主Sheet
        ISheet defSheet = workbook.getPrimarySheet();
        // 获取根Topic
        return defSheet.getRootTopic();
    }

    public static void download(List<TestCase> testCaseList,HttpServletResponse response,String fileName){
        //声明输出流
        OutputStream os = null;
        String workbookString = fileName;
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        IWorkbook workbook = builder.createWorkbook(workbookString);
        ISheet isheet = workbook.getPrimarySheet();
        isheet.setTitleText("sheet1");
        ITopic root = isheet.getRootTopic();
        root.setTitleText("title");
//        root.setHyperlink("hyperlink");
//        root.setStructureClass("structureClass");
//        root.setFolded(true);
        IBoundary boundary = workbook.createBoundary();
        boundary.setTitleText("boundary");
        root.addBoundary(boundary);

        //1.导出xmind模板
        if(CollectionUtils.isEmpty(testCaseList)){

            ITopic child1 = workbook.createTopic();
            child1.setTitleText("模块1");
            root.add(child1);

            ITopic child2 = workbook.createTopic();
            child2.setTitleText("模块2");
            root.add(child2);

            ITopic child11 = workbook.createTopic();
            child11.setTitleText("模块11");
            child1.add(child11);

            ITopic child12 = workbook.createTopic();
            child12.setTitleText("模块12");
            child1.add(child12);

            ITopic child21 = workbook.createTopic();
            child21.setTitleText("模块21");
            child2.add(child21);

            ITopic child22 = workbook.createTopic();
            child22.setTitleText("模块22");
            child2.add(child22);
        }else{
            for(TestCase testCase : testCaseList){
                ITopic child1 = workbook.createTopic();
                child1.setTitleText(testCase.getBelongToModule()+"");
                root.add(child1);

                ITopic child11 = workbook.createTopic();
                child11.setTitleText("tc-"+testCase.getTestCaseLevel());
                child1.add(child11);

                ITopic child111 = workbook.createTopic();
                child111.setTitleText("name:"+testCase.getCasename());
                child11.add(child111);

                ITopic child112 = workbook.createTopic();
                child112.setTitleText("pc:"+testCase.getPreCondition());
                child11.add(child112);

                ITopic child113 = workbook.createTopic();
                child113.setTitleText(testCase.getStep());
                child11.add(child113);

                ITopic child1131 = workbook.createTopic();
                child1131.setTitleText(testCase.getExpectedResults());
                child113.add(child1131);
            }
        }


        try {
            os = response.getOutputStream();
            workbook.save(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
