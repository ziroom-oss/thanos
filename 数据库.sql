/*
 Navicat Premium Data Transfer

 Source Server         : telot_ci
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : 10.30.7.152:3306
 Source Schema         : qu_thanos

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 08/11/2021 10:57:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auto_application_properties
-- ----------------------------
DROP TABLE IF EXISTS `auto_application_properties`;
CREATE TABLE `auto_application_properties`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
  `application_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用名称',
  `env` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '数据库类型.qua',
  `swagger_url` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'swagger地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '0未删除 1 已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_application_properties
-- ----------------------------

-- ----------------------------
-- Table structure for auto_data_database
-- ----------------------------
DROP TABLE IF EXISTS `auto_data_database`;
CREATE TABLE `auto_data_database`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
  `db_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库类型',
  `db_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `db_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库名称',
  `db_port` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '端口',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `user_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码?',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `application_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未定义' COMMENT '应用名称',
  `env` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '数据库类型.qa ,pre:预发环境',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '0未删除 1 已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_data_database
-- ----------------------------

-- ----------------------------
-- Table structure for auto_execution_record
-- ----------------------------
DROP TABLE IF EXISTS `auto_execution_record`;
CREATE TABLE `auto_execution_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `api_id` int(11) NULL DEFAULT NULL COMMENT '接口编号',
  `case_id` int(11) NULL DEFAULT NULL COMMENT '用例编号',
  `user_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编码',
  `record_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '记录名',
  `record_result` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '测试结果',
  `request_url` varchar(5120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口',
  `request_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求类型get post',
  `header` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求header',
  `response_header` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '返回header',
  `request_body` varchar(5120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求body',
  `actual_result` varchar(5120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实际结果',
  `start_time` datetime NULL DEFAULT NULL COMMENT '请求开始时间 时间戳',
  `response_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '响应结果时间 时间戳',
  `end_time` datetime NULL DEFAULT NULL COMMENT '执行完成时间 时间戳',
  `env` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `version` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除，1：删除 0；不删除',
  `data_type` int(4) NOT NULL DEFAULT 1 COMMENT 'SQL 0CASE 1API 2MULTI_API 3MAKE_DATA 6',
  `order_id` int(11) NULL DEFAULT NULL COMMENT '步骤执行顺序',
  `success_coverage` decimal(11, 1) NULL DEFAULT NULL COMMENT '成功覆盖率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_execution_record
-- ----------------------------

-- ----------------------------
-- Table structure for auto_execution_record_assert
-- ----------------------------
DROP TABLE IF EXISTS `auto_execution_record_assert`;
CREATE TABLE `auto_execution_record_assert`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `record_id` int(11) NULL DEFAULT NULL COMMENT '关联execution_record表',
  `assert_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '断言内容',
  `assert_type` int(11) NULL DEFAULT NULL COMMENT '断言类型',
  `assert_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '断言执行结果 SUCCESS/FAILURE',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_record_id`(`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_execution_record_assert
-- ----------------------------

-- ----------------------------
-- Table structure for auto_single_api
-- ----------------------------
DROP TABLE IF EXISTS `auto_single_api`;
CREATE TABLE `auto_single_api`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '接口ID',
  `system_id` int(11) NULL DEFAULT NULL COMMENT '系统ID',
  `module_id` int(11) NULL DEFAULT NULL COMMENT '模块ID',
  `api_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口名称',
  `request_uri` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求路径',
  `interfaceId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口地址ID，MD5加密',
  `request_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求类型',
  `header` varchar(5120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求头',
  `application_id` int(11) NULL DEFAULT 0 COMMENT 'omega 的应用ID；0代表历史数据',
  `controller_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '无分类' COMMENT '接口分类名称，一般指controller名称',
  `application_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `encrypt_type` int(1) NULL DEFAULT NULL,
  `is_rely` int(1) NULL DEFAULT 0 COMMENT '接口是否为依赖接口1为依赖接口0为非依赖接口',
  `rely_id` int(11) NULL DEFAULT NULL COMMENT '依赖API',
  `rely_mq_id` int(11) NULL DEFAULT NULL COMMENT '依赖MQ消息ID',
  `rely_es_id` int(11) NULL DEFAULT NULL COMMENT '关联es_search_condition主键id',
  `defender_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '维护人',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否已删除:0未删除，1：已删除',
  `create_user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_single_api
-- ----------------------------

-- ----------------------------
-- Table structure for auto_single_api_case
-- ----------------------------
DROP TABLE IF EXISTS `auto_single_api_case`;
CREATE TABLE `auto_single_api_case`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用例ID',
  `api_id` int(11) NOT NULL COMMENT '接口ID',
  `auto_sort` int(10) NULL DEFAULT NULL COMMENT '自动排序',
  `case_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用例名称',
  `request_param` varchar(5120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `request_body` varchar(5120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'post请求体',
  `expected_results` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '期望结果',
  `is_runnable` tinyint(1) NULL DEFAULT NULL COMMENT '1是运行，2是关闭',
  `pre_sql` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前置SQL',
  `post_sql` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后置SQL',
  `rely_id` int(11) NULL DEFAULT NULL,
  `rely_mq_id` int(11) NULL DEFAULT NULL COMMENT '依赖MQ消息id',
  `rely_es_id` int(11) NULL DEFAULT NULL COMMENT '关联es_search_condition主键id',
  `pre_request` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预处理所需字段 {appid:\"\", secret: \"\"}',
  `comment` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `case_origin` tinyint(4) NOT NULL DEFAULT 0 COMMENT '接口用例来源 0：人工录入 1：自动化创建',
  `create_user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `update_user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人',
  `interfaceId` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `protocol_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'http' COMMENT '协议类型',
  `case_type` tinyint(1) NULL DEFAULT NULL COMMENT '1:正常测试用例,0：异常测试用例',
  `deleted` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否删除:0是未删除，1是删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_single_api_case
-- ----------------------------

-- ----------------------------
-- Table structure for auto_single_assert
-- ----------------------------
DROP TABLE IF EXISTS `auto_single_assert`;
CREATE TABLE `auto_single_assert`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `case_id` int(11) NULL DEFAULT NULL COMMENT '关联auto_single_api_case表的id',
  `assert_content` varchar(5120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '断言内容',
  `assert_type` int(11) NULL DEFAULT NULL COMMENT '断言类型 0:包字符串 1：JSONPATH',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `case_id_index`(`case_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_single_assert
-- ----------------------------

-- ----------------------------
-- Table structure for auto_user_follow_application
-- ----------------------------
DROP TABLE IF EXISTS `auto_user_follow_application`;
CREATE TABLE `auto_user_follow_application`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `application_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_user_code`(`user_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of auto_user_follow_application
-- ----------------------------

-- ----------------------------
-- Table structure for daily_test_exe_report
-- ----------------------------
DROP TABLE IF EXISTS `daily_test_exe_report`;
CREATE TABLE `daily_test_exe_report`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `topic_id` bigint(20) NULL DEFAULT NULL COMMENT '项目id',
  `daily_id` bigint(20) NULL DEFAULT NULL COMMENT '日报id',
  `case_id` bigint(20) NULL DEFAULT NULL COMMENT 'caseId',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT 'taskId',
  `bug_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限id（bugid）',
  `bug_status_id` bigint(20) NULL DEFAULT NULL COMMENT 'bug状态id',
  `bug_level_id` bigint(20) NULL DEFAULT NULL COMMENT 'bug级别id',
  `exe_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行类型（未执行、跳过、通过、失败）',
  `case_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例key，测试用例唯一标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_topic_id`(`topic_id`) USING BTREE,
  INDEX `index_daily_id`(`daily_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4652 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目执行报表信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of daily_test_exe_report
-- ----------------------------

-- ----------------------------
-- Table structure for daily_test_report
-- ----------------------------
DROP TABLE IF EXISTS `daily_test_report`;
CREATE TABLE `daily_test_report`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `topic_id` bigint(20) NULL DEFAULT NULL COMMENT '项目id',
  `daily_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日报日期，格式yyyymmdd',
  `daily_risk_names` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '风险名称集合',
  `addresser` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发件人',
  `addressee` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人',
  `cc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '抄送人',
  `test_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提测版本',
  `test_stage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提测阶段',
  `email_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件备注',
  `send_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送邮箱组地址（格式邮箱地址1,邮箱地址2）',
  `cc_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '抄送邮箱组地址（格式邮箱地址1,邮箱地址2）',
  `bug_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目bug地址',
  `send_time` datetime NULL DEFAULT NULL COMMENT '邮件发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_topic_id`(`topic_id`) USING BTREE,
  INDEX `index_daily_date`(`daily_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1397844089888280579 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of daily_test_report
-- ----------------------------

-- ----------------------------
-- Table structure for daily_test_report_type
-- ----------------------------
DROP TABLE IF EXISTS `daily_test_report_type`;
CREATE TABLE `daily_test_report_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `daily_id` bigint(20) NULL DEFAULT NULL COMMENT '日报id',
  `test_execution_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试执行类型 例如：回归测试，功能测试',
  `testcase_count` int(10) NULL DEFAULT NULL COMMENT '测试用例总数',
  `run_success_count` int(10) NULL DEFAULT NULL COMMENT '执行成功总数',
  `run_fail_count` int(10) NULL DEFAULT NULL COMMENT '执行失败总数',
  `run_skip_count` int(10) NULL DEFAULT NULL COMMENT '运行跳过总数',
  `not_run_count` int(10) NULL DEFAULT NULL COMMENT '未执行总数',
  `bug_count` int(10) NULL DEFAULT NULL COMMENT 'bug总数',
  `bug_success_count` int(10) NULL DEFAULT NULL COMMENT '已解决bug数',
  `bug_unresolved_count` int(10) NULL DEFAULT NULL COMMENT '未解决bug数',
  `daily_count` int(10) NULL DEFAULT NULL COMMENT '当日用例总数',
  `daily_success_count` int(10) NULL DEFAULT NULL COMMENT '当日成功总数',
  `daily_fail_count` int(10) NULL DEFAULT NULL COMMENT '测试执行任务id',
  `daily_skip_count` int(10) NULL DEFAULT NULL COMMENT '当日跳过总数',
  `test_task_id` bigint(20) NULL DEFAULT NULL COMMENT '测试执行任务id',
  `test_task_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试执行任务名称',
  `bug_info_json` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'bug信息json',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_daily_id`(`daily_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 543 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of daily_test_report_type
-- ----------------------------

-- ----------------------------
-- Table structure for data_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `data_dictionary`;
CREATE TABLE `data_dictionary`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自增属性类型:枚举值',
  `english_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '英文名称',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `dic_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_dictionary
-- ----------------------------
INSERT INTO `data_dictionary` VALUES (1, 'application', 'PC', 'PC', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (2, 'application', 'mobile', 'APP', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (3, 'application', 'applets', '小程序', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (4, 'application', 'H5', 'H5', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (5, 'strategy', 'integrationTesting', '集成测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (6, 'strategy', 'smokeTesting', '冒烟测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (8, 'strategy', 'functionalTesting', '功能测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (9, 'strategy', 'regressionTesting', '回归测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (10, 'strategy', 'compatibilityTesting', '兼容性测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (11, 'environment', 'test', '测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (12, 'environment', 'dev', '开发', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (13, 'environment', 'gray', '灰度', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (14, 'environment', 'prod', '生产', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (15, 'strategy', 'performanceTesting', '性能测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (16, 'strategy', 'securityTesting', '安全性测试', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (17, 'testLevel', 'P0', '极高', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (18, 'testLevel', 'P1', '高', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (19, 'testLevel', 'P2', '中', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (20, 'testLevel', 'P3', '低', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (21, 'application', 'mwebsite', 'M站', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (24, 'fieldType', 'input', '单行文本框', 'zhangxl15', '2021-05-13 17:17:07', 'zhangxl15', '2021-05-13 17:17:07', NULL);
INSERT INTO `data_dictionary` VALUES (25, 'fieldType', 'select', '下拉选单', 'zhangxl15', '2021-05-13 17:17:07', 'zhangxl15', '2021-05-13 17:17:07', NULL);
INSERT INTO `data_dictionary` VALUES (26, 'fieldType', 'checkbox', '多选框', 'zhangxl15', '2021-05-13 17:17:07', 'zhangxl15', '2021-05-13 17:17:07', NULL);
INSERT INTO `data_dictionary` VALUES (27, 'fieldType', 'textarea', '多行文本框', 'zhangxl15', '2021-05-13 17:17:07', 'zhangxl15', '2021-05-13 17:17:07', NULL);
INSERT INTO `data_dictionary` VALUES (28, 'changeType', 'iteration', '日常迭代', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (29, 'changeType', 'demand', '需求变更', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (30, 'changeType', 'technology', '技术变更', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (31, 'application', 'server', '服务端', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (32, 'testCaseTop', 'testCaseTop', 'testCaseTop', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', '20');
INSERT INTO `data_dictionary` VALUES (33, 'endType', 'Service', '服务端', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (34, 'endType', 'FE', 'FE', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (35, 'endType', 'Android', 'Android端', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);
INSERT INTO `data_dictionary` VALUES (36, 'endType', 'IOS', 'IOS端', '60019044', '2021-04-22 12:48:02', '60019044', '2021-04-22 12:48:02', NULL);

-- ----------------------------
-- Table structure for demo_test
-- ----------------------------
DROP TABLE IF EXISTS `demo_test`;
CREATE TABLE `demo_test`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
  `demo_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库依赖demo演示的key',
  `demo_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '数据库依赖demo演示的value',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of demo_test
-- ----------------------------

-- ----------------------------
-- Table structure for monitoring
-- ----------------------------
DROP TABLE IF EXISTS `monitoring`;
CREATE TABLE `monitoring`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `monitor_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '埋点信息',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询/删除/修改/运行',
  `op_person` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人中文名称',
  `dept_ehr_code` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门编号',
  `user_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人系统号',
  `op_person_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱前缀',
  `op_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `server_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器IP',
  `server_ip_req_time` datetime NULL DEFAULT NULL COMMENT '服务器请求时间',
  `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端Ip',
  `client_ip_req_time` datetime NULL DEFAULT NULL COMMENT '客户端请求时\r\nclient_ip_req_time\r\n间',
  `req_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求UIL',
  `params` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `descript` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `exe_status` int(11) NULL DEFAULT NULL COMMENT '执行状态码',
  `exe_start_time` datetime NULL DEFAULT NULL COMMENT '执行开始时间',
  `exe_end_time` datetime NULL DEFAULT NULL COMMENT '执行结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1405059538963056371 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of monitoring
-- ----------------------------

-- ----------------------------
-- Table structure for omega_mq_exception
-- ----------------------------
DROP TABLE IF EXISTS `omega_mq_exception`;
CREATE TABLE `omega_mq_exception`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '外鍵ID',
  `msg_ext` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of omega_mq_exception
-- ----------------------------

-- ----------------------------
-- Table structure for system_componment
-- ----------------------------
DROP TABLE IF EXISTS `system_componment`;
CREATE TABLE `system_componment`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主键名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主键类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_componment
-- ----------------------------

-- ----------------------------
-- Table structure for system_componment_params
-- ----------------------------
DROP TABLE IF EXISTS `system_componment_params`;
CREATE TABLE `system_componment_params`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `componment_id` bigint(20) NULL DEFAULT NULL COMMENT '系统组件外键',
  `param_lable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公共参数名称',
  `param_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公共参数值',
  `param_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公共参数描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_componment_params
-- ----------------------------

-- ----------------------------
-- Table structure for system_module
-- ----------------------------
DROP TABLE IF EXISTS `system_module`;
CREATE TABLE `system_module`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模块ID',
  `system_module_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块类别：功能测试用例、接口测试用例',
  `system_domain_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统域名',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名称',
  `comment` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_module
-- ----------------------------

-- ----------------------------
-- Table structure for task_test_case
-- ----------------------------
DROP TABLE IF EXISTS `task_test_case`;
CREATE TABLE `task_test_case`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '执行任务主键',
  `test_case_id` bigint(20) NULL DEFAULT NULL COMMENT '测试用例ID',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `casename` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例名称',
  `test_case_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用例等级',
  `pre_condition` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前置条件',
  `step` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行步骤',
  `expected_results` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '期望结果',
  `version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例版本',
  `execution_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后执行人',
  `execution_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后执行结果',
  `relation_bug` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后关联bug',
  `execution_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行版本号：服务端的编译构建号',
  `execution_time` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `execution_number` bigint(20) NULL DEFAULT NULL COMMENT '执行次数',
  `delete_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标记',
  `case_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例key，测试用例唯一标识',
  `exe_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例执行备注',
  `test_execution_type` int(2) NULL DEFAULT 1 COMMENT '测试执行方法，1：人工，2：自动',
  `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_case_key`(`case_key`) USING BTREE COMMENT 'caseKey索引'
) ENGINE = InnoDB AUTO_INCREMENT = 20530 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of task_test_case
-- ----------------------------

-- ----------------------------
-- Table structure for test_app_user_rel
-- ----------------------------
DROP TABLE IF EXISTS `test_app_user_rel`;
CREATE TABLE `test_app_user_rel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用id',
  `email_pre` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱前缀',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_app_id`(`app_id`) USING BTREE,
  INDEX `index_email_pre`(`email_pre`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用用户关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_app_user_rel
-- ----------------------------

-- ----------------------------
-- Table structure for test_application
-- ----------------------------
DROP TABLE IF EXISTS `test_application`;
CREATE TABLE `test_application`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `ehr_tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织结构全路径',
  `application_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用类型：移动端、PC端、小程序、H5',
  `application_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  `application_hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联域名',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `ehr_group` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属测试组',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_application
-- ----------------------------

-- ----------------------------
-- Table structure for test_application_module
-- ----------------------------
DROP TABLE IF EXISTS `test_application_module`;
CREATE TABLE `test_application_module`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `application_id` bigint(20) NULL DEFAULT NULL COMMENT '所属应用外键',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属模块',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父级模块 module_code',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `level` int(1) NULL DEFAULT NULL COMMENT '所属模块级别',
  `module_tree_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块编码\r\n树状结构编码\r\nps:1001,1002,1003\r\n最后一级为上级的module_code',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_treePath`(`module_tree_path`) USING BTREE COMMENT 'treepath索引',
  INDEX `index_application_id`(`application_id`) USING BTREE,
  INDEX `index_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 179220428947458 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_application_module
-- ----------------------------

-- ----------------------------
-- Table structure for test_case
-- ----------------------------
DROP TABLE IF EXISTS `test_case`;
CREATE TABLE `test_case`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `casename` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例名称',
  `test_case_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用例等级',
  `pre_condition` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前置条件',
  `step` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行步骤',
  `expected_results` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '期望结果',
  `test_case_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例状态:标记',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例版本',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例类型：功能测试、回归测试、集成测试',
  `belong_to_system` bigint(20) NULL DEFAULT NULL COMMENT '所属系统',
  `belong_to_module` bigint(20) NULL DEFAULT NULL COMMENT '所属模块',
  `relation_requirement` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联需求',
  `approve_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例审批人',
  `approved_time` datetime NULL DEFAULT NULL COMMENT '用例审批时间',
  `ehr_tree_path` varchar(255) CHARACTER SET utf32 COLLATE utf32_general_ci NULL DEFAULT NULL COMMENT '组织结构全路径',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `flag` tinyint(1) NULL DEFAULT NULL COMMENT '是否为最新',
  `delete_flag` tinyint(1) NOT NULL COMMENT '删除标记',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `source_file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源文件存储路径',
  `source_file_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源文件存储key',
  `case_lock` tinyint(1) NULL DEFAULT NULL COMMENT '是否被锁定',
  `case_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用例业务唯一Key',
  `system_module_id` bigint(20) NULL DEFAULT NULL COMMENT '系统所属模块外键',
  `ehr_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属测试组',
  `belong_topic` bigint(20) NULL DEFAULT NULL COMMENT '所属项目（是一个跨平台的项目）',
  `belong_platform` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属平台（端） h5，小程序，pc，app',
  `change_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变更类型（给统计报表使用）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_case_key`(`case_key`) USING BTREE COMMENT 'caseKey索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_case
-- ----------------------------

-- ----------------------------
-- Table structure for test_config
-- ----------------------------
DROP TABLE IF EXISTS `test_config`;
CREATE TABLE `test_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `lable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'UI展示名称',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应实体的属性名称',
  `field_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型：单选、多选、输入框',
  `item_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子类型',
  `required` tinyint(1) NULL DEFAULT NULL COMMENT '是否为必填选项',
  `pool` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置池',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `system_config` tinyint(1) NULL DEFAULT 0 COMMENT '是否为系统配置',
  `field_type_lable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型中文名称：单选、多选、输入框',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_config
-- ----------------------------

-- ----------------------------
-- Table structure for test_config_options
-- ----------------------------
DROP TABLE IF EXISTS `test_config_options`;
CREATE TABLE `test_config_options`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `option_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性值',
  `default_selected` tinyint(1) NULL DEFAULT NULL COMMENT '默认选中',
  `bg_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '背景色',
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `descrition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `test_config_id` bigint(20) NULL DEFAULT NULL COMMENT '外键',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_config_options
-- ----------------------------

-- ----------------------------
-- Table structure for test_config_setting
-- ----------------------------
DROP TABLE IF EXISTS `test_config_setting`;
CREATE TABLE `test_config_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例配置组名称',
  `system_config` tinyint(1) NULL DEFAULT NULL COMMENT '是否是系统配置',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_config_setting
-- ----------------------------

-- ----------------------------
-- Table structure for test_execution
-- ----------------------------
DROP TABLE IF EXISTS `test_execution`;
CREATE TABLE `test_execution`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `case_id` bigint(20) NULL DEFAULT NULL COMMENT '执行测试用例ID',
  `execution_task_id` bigint(20) NULL DEFAULT NULL COMMENT '执行任务ID',
  `execution_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行结果',
  `execution_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行版本号：服务端的编译构建号',
  `relation_bug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联BUG',
  `execution_time` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `execution_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行人',
  `is_new` tinyint(1) NULL DEFAULT 0 COMMENT '用例是否为最新执行，1为最新执行，0为执行历史',
  `bug_status_id` bigint(20) NULL DEFAULT NULL COMMENT 'bug的状态id',
  `bug_level_id` bigint(20) NULL DEFAULT NULL COMMENT 'bug的优先级id',
  `case_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例key，测试用例唯一标识',
  `exe_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `test_execution_type` int(2) NULL DEFAULT 1 COMMENT '测试执行方法，1：人工，2：自动',
  `real_in_param` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'requestParam',
  `req_header` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'requestHeader',
  `req_body` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'requestBody',
  `actual_result` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实结果',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求地址',
  `env` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求环境',
  `request_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方式',
  `response_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '耗时（毫秒）',
  `assert_record_list` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '断言列表',
  `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6530 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_execution
-- ----------------------------

-- ----------------------------
-- Table structure for test_message
-- ----------------------------
DROP TABLE IF EXISTS `test_message`;
CREATE TABLE `test_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `create_user` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `msg_type` int(1) NULL DEFAULT NULL COMMENT '消息类型 1：用例库，2：用例执行，3：用例计划',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变化内容',
  `msg_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2180 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_message
-- ----------------------------

-- ----------------------------
-- Table structure for test_message_read
-- ----------------------------
DROP TABLE IF EXISTS `test_message_read`;
CREATE TABLE `test_message_read`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `create_user` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `msg_id` bigint(20) NULL DEFAULT NULL COMMENT '已读模块外键',
  `msg_status` int(1) NULL DEFAULT NULL COMMENT '消息状态 1: 已读 2: 未读',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_user`(`create_user`) USING BTREE,
  INDEX `index_msgid`(`msg_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_message_read
-- ----------------------------

-- ----------------------------
-- Table structure for test_plan
-- ----------------------------
DROP TABLE IF EXISTS `test_plan`;
CREATE TABLE `test_plan`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `test_plan_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划Key',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计划名称',
  `relation_requirement` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联需求',
  `test_environment` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试环境',
  `test_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试范围',
  `test_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试策略',
  `test_plan_master` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划负责人',
  `test_plan_start_time` datetime NULL DEFAULT NULL COMMENT '测试计划开始时间',
  `test_plan_end_time` datetime NULL DEFAULT NULL COMMENT '测试计划结束时间',
  `test_persion_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划人员',
  `test_risk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试风险',
  `test_plan_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划版本',
  `test_plan_status` int(1) NULL DEFAULT NULL COMMENT '计划状态：1.待提交 2.待审核 3.审核通过 4.审核拒绝',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记 1 删除 0 未删除',
  `lock_flag` tinyint(1) NULL DEFAULT 0 COMMENT '锁定标记 1 锁定 0 未锁定',
  `latest_flag` tinyint(1) NULL DEFAULT 1 COMMENT '是否最新标记 1 最新 0 非最新',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `approve_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批人',
  `approved_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_plan_name`(`plan_name`) USING BTREE,
  INDEX `index_test_plan_key`(`test_plan_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 780 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_plan
-- ----------------------------

-- ----------------------------
-- Table structure for test_risk
-- ----------------------------
DROP TABLE IF EXISTS `test_risk`;
CREATE TABLE `test_risk`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `risk_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '风险名称',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `risk_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '风险编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_risk_name`(`risk_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测试风险信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_risk
-- ----------------------------

-- ----------------------------
-- Table structure for test_task
-- ----------------------------
DROP TABLE IF EXISTS `test_task`;
CREATE TABLE `test_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务状态',
  `testcase_count` int(20) NULL DEFAULT NULL COMMENT '测试用例总数',
  `run_success_count` int(20) NULL DEFAULT NULL COMMENT '执行成功总数',
  `run_fail_count` int(20) NULL DEFAULT NULL COMMENT '执行失败总数',
  `run_count` int(20) NULL DEFAULT NULL COMMENT '执行总数',
  `not_run_count` int(20) NULL DEFAULT NULL COMMENT '未执行总数',
  `task_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试任务类型',
  `test_task_master` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试任务负责人',
  `relation_requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联需求',
  `ehr_tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织结构全路径',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `run_skip_count` int(20) NULL DEFAULT NULL COMMENT '运行跳过总数',
  `test_execution_rate` double(10, 2) NULL DEFAULT NULL COMMENT '测试执行率',
  `test_task_executors` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试执行人',
  `ehr_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属测试组',
  `test_execution_type` int(2) NULL DEFAULT 1 COMMENT '测试执行方法，1：人工，2：自动',
  `end_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端类型，服务端、ios、android、fe',
  `branch_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分支名称',
  `app_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appid（关联的项目id）',
  `parent_jira_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级jiraid',
  `template_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否是模板',
  `check_tag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '必验项标识，如果是必验项（统计使用）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `jiraAndTask_idx`(`relation_requirement`, `task_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 288 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_task
-- ----------------------------

-- ----------------------------
-- Table structure for test_topic
-- ----------------------------
DROP TABLE IF EXISTS `test_topic`;
CREATE TABLE `test_topic`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `topic_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试用例名称',
  `relation_requirement` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联需求',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `topic_master` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `up_time` datetime NULL DEFAULT NULL COMMENT '上线时间',
  `topic_participant` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参与人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_topic_name`(`topic_name`) USING BTREE,
  INDEX `index_relation_requirement`(`relation_requirement`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_topic
-- ----------------------------

-- ----------------------------
-- Table structure for topic_risk_rel
-- ----------------------------
DROP TABLE IF EXISTS `topic_risk_rel`;
CREATE TABLE `topic_risk_rel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `topic_id` bigint(20) NULL DEFAULT NULL COMMENT '项目id',
  `risk_id` bigint(20) NULL DEFAULT NULL COMMENT '风险id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_topic_id`(`topic_id`) USING BTREE,
  INDEX `index_risk_id`(`risk_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 188 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目风险关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_risk_rel
-- ----------------------------

-- ----------------------------
-- Table structure for topic_task_rel
-- ----------------------------
DROP TABLE IF EXISTS `topic_task_rel`;
CREATE TABLE `topic_task_rel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `topic_id` bigint(20) NULL DEFAULT NULL COMMENT '项目id',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '测试执行任务id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_topic_id`(`topic_id`) USING BTREE,
  INDEX `index_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 127 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_task_rel
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '员工编号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱前缀',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织结构完整路径',
  `ehr_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属的组织结构最后一层',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户角色',
  `user_type` int(1) NULL DEFAULT NULL COMMENT '外包人员：2正式；3外包',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_uid`(`uid`) USING BTREE,
  UNIQUE INDEX `uni_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 252 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `qu_thanos`.`user` (`id`, `uid`, `user_name`, `nick_name`, `tree_path`, `ehr_group`, `create_time`, `update_time`, `role`, `user_type`, `password`) VALUES (1, '10000', 'zhangsan', '张三', '10000', '软件研发部', '2021-10-26 18:14:19', '2021-11-03 10:40:12', 'superAdmin', 2, 'e10adc3949ba59abbe56e057f20f883e');
-- 密码为：123456
SET FOREIGN_KEY_CHECKS = 1;
