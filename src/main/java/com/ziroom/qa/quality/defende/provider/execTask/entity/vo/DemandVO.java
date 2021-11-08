package com.ziroom.qa.quality.defende.provider.execTask.entity.vo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.JiraGroupDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DemandVO {

    /**
     * 发现阶段
     *
     * 测试阶段
     * 线上问题
     */
    private List<String> discoverStageList = new ArrayList<>();

    /**
     * 缺陷类型
     *
     * 代码错误
     * 产品设计缺陷
     * 产品优化建议
     * 技术设计缺陷
     * 数据原因
     * 第三方非自如系统原因
     * 系统故障
     * 硬件问题
     * 产品需求变更
     * UI设计问题
     * 业务流程优化
     * 配置错误
     * 安全问题
     * 环境问题
     * 操作错误
     * 性能问题
     * 技术变更
     * 合规整改
     */
    private List<String> bugTypeList = Arrays.asList("代码错误","产品设计缺陷","产品优化建议","技术设计缺陷","数据原因","第三方非自如系统原因","系统故障","硬件问题","产品需求变更","UI设计问题","业务流程优化","配置错误","安全问题","环境问题","操作错误","性能问题","技术变更","合规整改");

    /**
     * 缺陷发现端类型
     *
     前端H5
     后台
     iOS
     Android
     嵌入式
     */
    private List<String> bugEndTypeList = Arrays.asList("前端H5","后台","iOS","Android","嵌入式");

    /**
     * 缺陷年龄的引入阶段
     *
     * 历史遗留问题
     * 需求阶段产生
     * 编码阶段产生
     * 需求变更引入
     * 缺陷修改引入
     * 线上运营阶段
     * 技术设计阶段引入
     */
    private List<String> bugAgeStageList = new ArrayList<>();

    /**
     * 缺陷所属中心
     *
     "技术平台保障中心","智能家装O2O中心","自如家服平台","企业信息平台中心","IOT平台中心","资管中心","设计中心","数据中台中心","业主O2O中心","租住O2O中心","运营中台中心","用户体验增长中心","大前端研发中心","质量与信息安全中心","其他"
     */
    private List<String> bugCenterList = Arrays.asList("技术平台保障中心","智能家装O2O中心","自如家服平台","企业信息平台中心","IOT平台中心","资管中心","设计中心","数据中台中心","业主O2O中心","租住O2O中心","运营中台中心","用户体验增长中心","大前端研发中心","质量与信息安全中心","其他");

    /**
     * 缺陷责任部门
     *
     */
    private List<JiraGroupDTO> bugDeptList = new ArrayList<>();

    /**
     * 模块（多选）
     *
     * 无
     * 其他
     */
    private List<String> moduleList = Arrays.asList("无","其他");

    /**
     * 缺陷优先级
     *
     * 极高
     * 高
     * 中
     * 低
     */
    private List<String> bugLevelList = Arrays.asList("极高","高","中","低");

}
