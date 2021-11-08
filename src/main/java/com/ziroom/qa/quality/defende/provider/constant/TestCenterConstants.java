package com.ziroom.qa.quality.defende.provider.constant;

/**
 * 测试平台常量
 */
public interface TestCenterConstants {


    /**
     * 限制消息查询的总量，默认100条
     */
    int LIMIT_COUNT = 100;

    /**
     * 消息读取状态 1 已读 2 未读
     */
    int MSG_STATUS_READ = 1;

    /**
     * 消息读取状态 1 已读 2 未读
     */
    int MSG_STATUS_UNREAD = 2;

    /**
     * 默认返回失败
     */
    String RES_MSG_ERROR = "失败";

    /**
     * 参数为空
     */
    String RES_MSG_PARAMS_EMPTY = "入参为空";

    /**
     * 查询数据为空
     */
    String RES_MSG_SEARCH_EMPTY = "查询数据为空";

    /**
     * 默认返回成功
     */
    String RES_MSG_SUCCESS = "成功";


    /**
     * 成功,数据无变动
     */
    String RES_MSG_NOCHANGE_SUCCESS = "成功,数据无变动";

    /**
     * 该数据已存在
     */
    String RES_MSG_REPPEAT = "该数据已存在";

    /**
     * 数据有关联无法删除
     */
    String RES_MSG_RELATION = "数据有关联无法删除";

    /**
     * 需要处理的数据为空
     */
    String RES_MSG_APPROVED_EMPTY = "需要处理的数据为空";

    /**
     * 有审核通过不允许删除
     */
    String RES_MSG_APPROVED_PASS_NODEL = "有审核通过不允许删除";

    /**
     * 有关联数据
     */
    String RES_MSG_RELATION_DATA = "有关联数据";

    /**
     * 用例状态不正确
     */
    String RES_MSG_STATUS_NO_RIGHT = "用例状态不正确";

    /**
     * jiraid认证不通过
     */
    String RES_MSG_JIRA_ERROR = "JIRAID认证不通过";

    /**
     * jiraid不存在
     */
    String RES_MSG_JIRA_NOEXIST = "JIRAID不存在";

    /**
     * 子部门信息不存在
     */
    String RES_MSG_CHILDDEPT_NOEXIST = "子部门信息不存在";

    /**
     * issue中bug的类型id
     */
    Long ISSUE_TYPE_BUG = 10005L;




    /**
     * 测试执行模板不存在
     */
    String TESTEXE_TEMPLATE_NO_EXIST = "测试执行模板不存在！！！";

    /**
     * 测试执行模板没有关联用例
     */
    String TESTEXE_TEMPLATE_NO_CASE = "测试执行模板没有关联用例！！！";
}
