CREATE TABLE `user_commodity_favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `status` varchar(50) DEFAULT 'active' COMMENT '1-正常收藏 0-取消收藏',
  `remark` varchar(255) DEFAULT NULL COMMENT '用户备注',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_favorite` (`userId`,`commodityId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1966807565908320259 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
