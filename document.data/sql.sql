# 提工单用
update test_case set id= 1 where id=1;
drop table if exists auto_data_database ;
create table auto_data_database(
    id int auto_increment comment '数据库ID' primary key,
    db_type varchar(255) not null comment '数据库类型',
    db_ip varchar(255) not null comment '',
    db_name varchar(255) not null comment '数据库名称',
    db_port varchar(255) not null comment '端口',
    user_name varchar(255) not null comment '用户名',
    user_password varchar(255) not null comment '密码?',
    description varchar(255) null comment '描述',
    application_name varchar(256) default '未定义' not null comment '应用名称',
    env varchar(11) default '' null comment '数据库类型.qa ,pre:预发环境',
    create_time datetime default now() comment '插入时间',
    update_time datetime default now() comment '修改时间',
    deleted tinyint(1) default 0 null comment '0未删除 1 已删除'
)charset=utf8;

drop table if exists auto_single_api_case;
create table auto_single_api_case(
    id               int auto_increment comment '用例ID' primary key,
    api_id           int                                         not null comment '接口ID',
    auto_sort        int(10)                                     null comment '自动排序',
    case_name        varchar(512)                               not null comment '用例名称',
    request_param    varchar(5120)                               null comment '请求参数',
    request_body     varchar(5120)               default ''      null comment 'post请求体',
    expected_results varchar(1)                               null comment '期望结果',
    is_runnable      tinyint(1)                                  null comment '1是运行，2是关闭',
    pre_sql          varchar(1024)                               null comment '前置SQL',
    post_sql         varchar(1024)                               null comment '后置SQL',
    rely_id          int                                         null comment '',
    rely_mq_id       int                                         null comment '依赖MQ消息id',
    rely_es_id       int                                         null comment '关联es_search_condition主键id',
    pre_request      varchar(255)                                null comment '预处理所需字段 {appid:"", secret: ""}',
    comment          varchar(64)                               null comment '备注',
    case_origin      tinyint                      default 0      not null comment '接口用例来源 0：人工录入 1：自动化创建',
    create_user_code varchar(50)                                 not null comment '创建人',
    update_user_code varchar(50)                                 not null comment '更新人',
    interfaceId      varchar(1)                                null,
    protocol_type    varchar(50)                  default 'http' null comment '协议类型',
    case_type        tinyint(1)                                  null comment '1:正常测试用例,0：异常测试用例',
    deleted           tinyint(1) unsigned zerofill default 0      not null comment '是否删除:0是未删除，1是删除',
    create_time datetime default now() comment '插入时间',
    update_time datetime default now() comment '修改时间'
);
drop table if exists auto_single_api;
create table auto_single_api(
    id int auto_increment comment '接口ID'
        primary key,
    system_id int null comment '系统ID',
    module_id int null comment '模块ID',
    api_name varchar(255) null comment '接口名称',
    request_uri varchar(4096) not null comment '请求路径',
    interfaceId varchar(255) null comment '接口地址ID，MD5加密',
    request_type varchar(50) not null comment '请求类型',
    header varchar(5120) null comment '请求头',
    application_id int default 0 null comment 'omega 的应用ID；0代表历史数据',
    controller_name varchar(256) default '无分类' not null comment '接口分类名称，一般指controller名称',
    application_name varchar(256) not null comment '应用名称',
    encrypt_type int(1) null comment '',
    is_rely int(1) default 0 null comment '接口是否为依赖接口1为依赖接口0为非依赖接口',
    rely_id int null comment '依赖API',
    rely_mq_id int null comment '依赖MQ消息ID',
    rely_es_id int null comment '关联es_search_condition主键id',
    defender_code varchar(50) null comment '维护人',
    deleted tinyint(1) default 0 null comment '是否已删除:0未删除，1：已删除',
    create_user_code varchar(50) null comment '创建人',
    update_user_code varchar(50) null comment '更新人',
    create_time datetime default now() comment '插入时间',
    update_time datetime default now() comment '修改时间'
);

drop table if exists auto_single_assert;
create table auto_single_assert
(
    id int auto_increment
        primary key,
    case_id int null comment '关联auto_single_api_case表的id',
    assert_content varchar(5120) null comment '断言内容',
    assert_type int null comment '断言类型 0:包字符串 1：JSONPATH',
    create_time datetime default now() comment '创建时间',
    update_time datetime default now() comment '更新时间',
    deleted int null comment '是否删除',
    index case_id_index(case_id)
);

drop table if exists auto_execution_record;
create table auto_execution_record(
    id int auto_increment comment '编号'
        primary key,
    api_id int null comment '接口编号',
    case_id int null comment '用例编号',
    user_code varchar(50) null comment '用户编码',
    record_name varchar(255) null comment '记录名',
    record_result varchar(50) null comment '测试结果',
    request_url varchar(5120) null comment '接口',
    request_type varchar(16) null comment '请求类型get post',
    header varchar(512) null comment '请求header',
    response_header varchar(1024) null comment '返回header',
    request_body varchar(5120) null comment '请求body',
    actual_result varchar(5120) charset utf8mb4 null comment '实际结果',
    start_time datetime null comment '请求开始时间 时间戳',
    response_time varchar(255) null comment '响应结果时间 时间戳',
    end_time datetime null comment '执行完成时间 时间戳',
    env varchar(32) null,
    version varchar(13) null comment '版本',
    deleted tinyint(1) default 0 null comment '是否删除，1：删除 0；不删除',
    data_type int(4) default 1 not null comment 'SQL 0CASE 1API 2MULTI_API 3MAKE_DATA 6',
    order_id int null comment '步骤执行顺序'
)charset=utf8;
alter table auto_execution_record add column success_coverage decimal(11,1) comment '成功覆盖率';

create index api_id_index
    on auto_execution_record (api_id);
create index case_id_index
    on auto_execution_record (case_id);
create index index_systemCode
    on auto_execution_record (user_code);

create table auto_execution_record_assert
(
    id int auto_increment
        primary key,
    record_id int null comment '关联execution_record表',
    assert_content varchar(1024) null comment '断言内容',
    assert_type int null comment '断言类型',
    assert_result varchar(255) null comment '断言执行结果 SUCCESS/FAILURE'
);
create index index_record_id
    on auto_execution_record_assert (record_id);

create table auto_user_follow_application(
    id int auto_increment
        primary key,
    user_code varchar(64) not null comment '用户ID',
    application_code varchar(128) not null comment '应用码',
    create_time timestamp default CURRENT_TIMESTAMP not null,
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);
create index index_user_code
    on auto_user_follow_application (user_code);

drop table if exists omega_mq_exception;
create table omega_mq_exception
(
    id int unsigned auto_increment
        primary key,
    msg_id varchar(255) not null comment '外鍵ID',
    msg_ext varchar(10240) null,
    create_time datetime null
);
create table auto_application_properties
(
    id               int auto_increment comment '数据库ID'
        primary key,
    application_name varchar(256) not null comment '应用名称',
    env              varchar(11)  default '' null comment '数据库类型.qua',
    swagger_url      varchar(512) default '' null comment 'swagger地址',
    create_time      datetime     default CURRENT_TIMESTAMP null comment '插入时间',
    update_time      datetime     default CURRENT_TIMESTAMP null comment '修改时间',
    deleted          tinyint(1) default 0 null comment '0未删除 1 已删除'
)charset=utf8;
create table demo_test(
      id               int auto_increment comment '数据库ID'
          primary key,
      demo_key varchar(256) not null comment '数据库依赖demo演示的key',
      demo_value varchar(256)  default '' null comment '数据库依赖demo演示的value'

);

alter table test_execution modify exe_remark varchar(1000) comment '备注',
    modify assert_record_list varchar(5000) comment '断言列表';


select * from auto_data_database ;
