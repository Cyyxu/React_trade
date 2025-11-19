CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
  `postId` bigint(20) NOT NULL COMMENT '面经帖子 ID',
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parentId` bigint(20) DEFAULT NULL COMMENT '父评论 ID，支持多级嵌套回复',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ancestorId` bigint(20) DEFAULT NULL COMMENT '祖先评论ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `comment_questionId` (`postId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1966828939087572994 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
