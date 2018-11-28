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
