CREATE TABLE `private_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
  `senderId` bigint(20) NOT NULL COMMENT '发送者 ID',
  `recipientId` bigint(20) NOT NULL COMMENT '接收者 ID',
  `content` varchar(4096) DEFAULT NULL COMMENT '消息内容(UTF8MB4 支持Emoji表情)',
  `alreadyRead` tinyint(4) DEFAULT '0' COMMENT '0-未阅读 1-已阅读',
  `type` varchar(255) NOT NULL COMMENT '消息发送类型（用户发送还是管理员发送,user Or admin)枚举',
  `isRecalled` tinyint(4) DEFAULT '0' COMMENT '是否撤回  0-未撤回 1-已撤回',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1901154100096696323 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
