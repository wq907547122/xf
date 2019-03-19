-- 用户模块的sql语句
-- 用户信息
drop table if exists tb_user;
CREATE TABLE `tb_user` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(64) NOT NULL COMMENT '登录名，登录名称唯一',
  `password` varchar(32) NOT NULL COMMENT '登录密码',
  `salt` varchar(32) DEFAULT NULL COMMENT '盐值',
  `nice_name` varchar(255) NOT NULL COMMENT '用户名用于显示，类似昵称',
  `gender` char(1) DEFAULT NULL COMMENT '性别 0： 男 1：女',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号码',
  `status` tinyint(4) DEFAULT '0' COMMENT '用户状态：0:正常 1:禁用',
  `user_type` tinyint(4) DEFAULT '1' COMMENT '用户类型：0:企业用户 1:普通用户 2：系统管理员;  默认是普通用户1',
  `enterprise_id` bigint(16) DEFAULT NULL COMMENT '企业id,外键对应企业表的id,企业对应用户是1对多的关系',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `qq` varchar(64) DEFAULT NULL COMMENT '用户QQ',
  `create_user_id` bigint(16) DEFAULT NULL COMMENT '创建用户id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user_id` bigint(16) DEFAULT NULL COMMENT '修改时间',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';
-- 新增用户信息
INSERT INTO `tb_user` (`id`, `login_name`, `password`, `salt`, `nice_name`, `gender`, `phone`, `status`, `user_type`, `enterprise_id`, `email`, `qq`, `create_user_id`, `create_date`, `modify_user_id`, `modify_date`) VALUES ('1', 'admin', '62b88757bf1fd2151a63638c3895fa0f', 'interest', '系统管理员', '0', NULL, '0', '2', NULL, NULL, NULL, NULL, '2019-01-15 10:22:21', NULL, NULL);


-- 用户与角色的关系表(多对多)
drop table if exists tb_user_role;
CREATE TABLE `tb_user_role` (
  `user_id` bigint(16) NOT NULL COMMENT 't_user表的id',
  `role_id` bigint(16) NOT NULL COMMENT 't_role表的id',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与角色关系表(多对多)';

-- 角色表
drop table if exists tb_role;
CREATE TABLE `tb_role` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '角色名',
  `role_type` varchar(32) DEFAULT NULL COMMENT '角色类型: system:系统管理员角色 enterprise：企业管理员角色 operator：运维人员角色 normal：普通人员角色',
  `description` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(4) DEFAULT '0' COMMENT '角色状态：0:启用 1:禁用',
  `enterprise_id` bigint(16) DEFAULT NULL COMMENT '企业id',
  `create_user_id` bigint(16) DEFAULT NULL COMMENT '创建用户id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user_id` bigint(16) DEFAULT NULL COMMENT '修改人id',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='角色表';
INSERT INTO `tb_role` (`id`, `name`, `role_type`, `description`, `status`, `enterprise_id`, `create_user_id`, `create_date`, `modify_user_id`, `modify_date`) VALUES ('1', '系统管理员', 'system', '给自己公司使用的超级管理员', '0', NULL, NULL, '2019-01-23 15:39:45', NULL, NULL);
INSERT INTO `tb_role` (`id`, `name`, `role_type`, `description`, `status`, `enterprise_id`, `create_user_id`, `create_date`, `modify_user_id`, `modify_date`) VALUES ('2', '企业管理员', 'enterprise', '企业管理员', '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_user_role` (`user_id`, `role_id`) VALUES ('1', '1');


-- 权限表
drop table if exists tb_auth;
CREATE TABLE `tb_auth` (
  `id` bigint(16) NOT NULL COMMENT '权限ID',
  `auth_name` varchar(64) DEFAULT NULL COMMENT '权限名称',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `role_type` smallint(1) DEFAULT '0' COMMENT '权限类型：0：web端(系统权限) 1：终端(app权限); 默认0',
  `sys_auth` smallint(1) DEFAULT '0' COMMENT '权限是否是系统预置权限:  1：预置  0：后来添加; 默认0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';


-- 权限资源表（t_auth与他的对应关系是一对多）
drop table if exists tb_resource_info;
create table tb_resource_info
(
   id                   bigint(16) not null auto_increment,
   resource_key         varchar(64) not null comment '资源的key值，唯一',
   auth_id              bigint(16) default NULL comment 't_auth权限表的ID',
   resource_name        varchar(64) default NULL comment '资源名称',
   description          varchar(255) default NULL comment '资源描述',
   auth_url             varchar(255) not null comment '资源的URL路径',
   request_type         varchar(16) default NULL comment '资源的请求类型 POST:post请求 GET:get请求 PUT:put请求 DELETE:delete请求',
   unique               (resource_key),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '权限的资源表';

-- 角色与权限(多对多)
drop table if exists tb_role_auth;
CREATE TABLE `tb_role_auth` (
  `role_id` bigint(16) NOT NULL comment 't_role表的id',
  `auth_id` bigint(16) NOT NULL comment 't_auth表的id',
  PRIMARY KEY (`role_id`,`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '角色与权限的中间关系表';
-- 企业表
drop table if exists tb_enterprise;
CREATE TABLE `tb_enterprise` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(225) NOT NULL COMMENT '企业名称',
  `description` varchar(1000) DEFAULT NULL COMMENT '企业简介',
  `parent_id` bigint(16) DEFAULT NULL COMMENT '父ID, 用于确定上级企业',
  `avatar_path` varchar(255) DEFAULT NULL COMMENT '企业缩影图片',
  `address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `contact_people` varchar(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(64) DEFAULT NULL COMMENT '联系方式',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `device_limit` int(8) DEFAULT '0' COMMENT '接入设备限制数',
  `user_limit` int(6) DEFAULT '0' COMMENT '企业下属用户限制数',
  `create_user_id` bigint(16) DEFAULT NULL COMMENT '创建用户ID',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user_id` bigint(16) DEFAULT NULL COMMENT '修改用户ID',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '区域经度，用于地图汇聚',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `radius` decimal(10,3) DEFAULT NULL COMMENT '区域半径，地图呈现区域范围大小',
  `logo` varchar(255) DEFAULT NULL COMMENT '企业LOGO',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='企业表';

-- 电站
drop table if exists tb_station_info;
CREATE TABLE `tb_station_info` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `station_code` varchar(32) NOT NULL COMMENT '电站编号',
  `station_name` varchar(255) NOT NULL COMMENT '电站名称',
  `installed_capacity` decimal(20,4) DEFAULT NULL COMMENT '装机容量kW',
  `station_build_status` tinyint(4) DEFAULT NULL COMMENT '电站状态：1:并网 2:在建 3:规划',
  `produce_date` date DEFAULT NULL COMMENT '投产时间',
  `online_type` tinyint(4) DEFAULT NULL COMMENT '并网类型：1:地面式 2:分布式 3:户用',
  `station_type` tinyint(4) DEFAULT NULL COMMENT '电站类型：1:渔光、2:农光、3:牧光',
  `inverter_type` tinyint(4) DEFAULT NULL COMMENT '逆变器类型：0:集中式 1:组串式 2:户用',
  `install_angle` decimal(6,2) DEFAULT NULL COMMENT '安装角度',
  `assembly_layout` tinyint(1) DEFAULT NULL COMMENT '组件布置方式：1:横排 2:竖排',
  `floor_space` decimal(16,6) DEFAULT NULL COMMENT '占地面积平方千米',
  `amsl` decimal(20,2) DEFAULT NULL COMMENT '平均海拔',
  `life_cycle` tinyint(4) DEFAULT NULL COMMENT '计划运营年限',
  `safe_run_datetime` bigint(16) DEFAULT NULL COMMENT '安全运行开始时间（精确到日的毫秒数）',
  `is_poverty_relief` tinyint(1) DEFAULT '0' COMMENT '是否扶贫站：0:不是 1:是',
  `station_file_id` varchar(32) DEFAULT NULL COMMENT '电站缩略图',
  `station_addr` varchar(255) DEFAULT NULL COMMENT '电站详细地址',
  `station_desc` varchar(1000) DEFAULT NULL COMMENT '电站简介',
  `contact_people` varchar(64) DEFAULT NULL COMMENT '联系人',
  `phone` varchar(32) DEFAULT NULL COMMENT '联系方式',
  `station_price` decimal(7,3) DEFAULT NULL COMMENT '电价',
  `latitude` decimal(12,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(12,6) DEFAULT NULL COMMENT '经度',
  `time_zone` int(11) DEFAULT NULL COMMENT '电站所在时区',
  `area_code` varchar(64) DEFAULT NULL COMMENT '行政区域编号（对应省市县编号以@符号连接）',
  `enterprise_id` bigint(16) DEFAULT NULL COMMENT '企业编号',
  `domain_id` bigint(16) DEFAULT NULL COMMENT '区域编号',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '电站是否逻辑删除 0：未删除 1：已删除',
  `create_user_id` bigint(16) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_user_id` bigint(16) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `is_monitor` tinyint(1) DEFAULT '0' COMMENT '设备是否来源于监控 1:监控的电站; 0：集维侧的电站',
  PRIMARY KEY (`id`),
  KEY `idx_station_code` (`station_code`),
  KEY `idx_station_name` (`station_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='电站表';
-- 电站与用户(多对多)
drop table if exists tb_user_station;
create table tb_user_station
(
   station_code         varchar(64) not null,
   user_id              bigint(16) not null,
   primary key (station_code, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '用户与电站的中间关系表';

-- 区域
drop table if exists tb_domain_info;
CREATE TABLE `tb_domain_info` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '区域名',
  `description` varchar(1000) DEFAULT NULL COMMENT '区域描述',
  `parent_id` bigint(16) DEFAULT NULL COMMENT '父ID',
  `enterprise_id` bigint(16) DEFAULT NULL COMMENT '企业编号',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '区域经度，用于地图汇聚',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `radius` decimal(10,3) DEFAULT NULL COMMENT '区域半径，地图呈现区域范围大小',
  `domain_price` decimal(7,3) DEFAULT NULL COMMENT '区域电价，计算收益是会使用',
  `currency` varchar(16) DEFAULT NULL COMMENT '货币：￥ $ €',
  `create_user_id` bigint(16) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_user_id` bigint(16) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  `path` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';