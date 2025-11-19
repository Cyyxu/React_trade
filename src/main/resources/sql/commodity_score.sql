CREATE TABLE `commodity_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品评分 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `score` int(11) NOT NULL COMMENT '评分（0-5，星级评分）',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `scoreId` (`commodityId`,`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1966816927993303042 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
