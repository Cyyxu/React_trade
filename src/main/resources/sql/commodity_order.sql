CREATE TABLE `commodity_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `remark` varchar(1024) DEFAULT NULL COMMENT '订单备注',
  `buyNumber` int(11) DEFAULT NULL COMMENT '购买数量',
  `paymentAmount` decimal(10,2) DEFAULT NULL COMMENT '订单总支付金额',
  `payStatus` tinyint(4) DEFAULT '0' COMMENT '0-未支付 1-已支付',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1967076907777822722 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
