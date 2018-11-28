CREATE DATABASE `db_conc_lock` DEFAULT CHARACTER SET utf8;

USE `db_conc_lock`;

CREATE TABLE IF NOT EXISTS `db_conc_lock`.`demo`(
  `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '自增主键',
  `type`            INT             NOT NULL DEFAULT 0       COMMENT '类型',
  `content`         VARCHAR(128)    NOT NULL DEFAULT ''      COMMENT '内容',
  `create_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '创建时间',
  `update_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `db_conc_lock`.`product`(
  `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '自增主键',
  `type`            INT             NOT NULL DEFAULT 0       COMMENT '类型',
  `name`            VARCHAR(32)     NOT NULL DEFAULT ''      COMMENT '名称',
  `stock`           INT             NOT NULL DEFAULT 0       COMMENT '库存数量',
  `price`           INT             NOT NULL DEFAULT 0       COMMENT '单价',
  `version`         BIGINT          NOT NULL DEFAULT 0       COMMENT '版本号',
  `create_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '创建时间',
  `update_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

insert into `db_conc_lock`.`product` values('2018112815220001','1','小米8限时抢购','5','199900','0','1543389825000',
'1543389825000');
insert into `db_conc_lock`.`product` values('2018112815220002','1','小米电视4S限时抢购','5','249900','0','1543389825000',
'1543389825000');

CREATE TABLE IF NOT EXISTS `db_conc_lock`.`order_item`(
  `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '自增主键',
  `user_id`         VARCHAR(64)     NOT NULL DEFAULT ''      COMMENT '用户id',
  `product_id`      BIGINT          NOT NULL DEFAULT 0       COMMENT '产品id',
  `number`          INT             NOT NULL DEFAULT 0       COMMENT '购买数量',
  `source`          VARCHAR(16)     NOT NULL DEFAULT ''      COMMENT '请求来源',
  `create_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '创建时间',
  `update_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
