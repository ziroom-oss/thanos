package com.ziroom.qa.quality.defende.provider.vo.testcase;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseUploadVo {

    /**
     * jiraid
     */
    private String relationRequirement;

    /**
     * 对应的service
     */
    private TestCaseService testCaseService;

    /**
     * 上传结果集
     */
    private RestResultVo<TestCase> restResultVo;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 测试用例所属应用
     */
    private Long belongToSystem;

    /**
     * 关联所属模块的ID ： test_application_module 的id字段
     */
    private Long belongToModule;


    /**
     * 所属平台
     */
    private String belongPlatform;


    /**
     * 用例类型
     */
    private String type;

    /**
     * 上传校验flag，true：需要校验名称重复，false：不校验名称重复，重复的用例直接覆盖
     */
    private boolean uploadFlag;

}
