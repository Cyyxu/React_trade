-- 用户信息表
CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) NOT NULL COMMENT '账号',
  `userPassword` varchar(512) NOT NULL COMMENT '密码',
  `unionId` varchar(256) DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) DEFAULT NULL COMMENT '公众号openId',
  `userName` varchar(256) DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `userPhone` varchar(255) DEFAULT NULL COMMENT '联系电话',
  `userEmail` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `userSchool` varchar(255) DEFAULT NULL COMMENT '学校',
  `userMajor` varchar(255) DEFAULT NULL COMMENT '专业',
  `userAddress` varchar(500) DEFAULT NULL COMMENT '地址',
  `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1979893281187266563 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户';

-- 用户AI消息表
CREATE TABLE `user_ai_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userInputText` varchar(4096) NOT NULL COMMENT '用户输入',
  `aiGenerateText` text COMMENT 'AI生成的文本',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1979826502156980227 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户AI消息';

-- 用户商品收藏表
CREATE TABLE `user_commodity_favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `status` tinyint(4) DEFAULT '1' COMMENT '1-正常收藏 0-取消收藏',
  `remark` varchar(255) DEFAULT NULL COMMENT '用户备注',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1966807565908320259 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户商品收藏';

-- 私聊消息表
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1901154100096696323 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='私聊消息';

-- 帖子点赞表
CREATE TABLE `post_thumb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1967076933170139139 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帖子点赞';

-- 帖子收藏表
CREATE TABLE `post_favour` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1967076929663700994 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帖子收藏';

-- 帖子表
CREATE TABLE `post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `tags` varchar(1024) DEFAULT NULL COMMENT '标签列表（json 数组）',
  `thumbNum` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1966375111917641730 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='帖子';

-- 公告表
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `noticeTitle` varchar(255) NOT NULL COMMENT '公告标题',
  `noticeContent` varchar(255) NOT NULL COMMENT '公告内容',
  `noticeAdminId` bigint(20) NOT NULL COMMENT '创建人id（管理员）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1960983017402449923 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公告';

-- 商品分类表
CREATE TABLE `commodity_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品分类 ID',
  `typeName` varchar(255) NOT NULL COMMENT '商品类别名称',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1900438197696618498 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品分类';

-- 商品评分表
CREATE TABLE `commodity_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品评分 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `score` int(11) NOT NULL COMMENT '评分（0-5，星级评分）',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1966816927993303042 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 商品订单表
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1967076907777822722 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品订单';

-- 商品表
CREATE TABLE `commodity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品 ID',
  `commodityName` varchar(255) NOT NULL COMMENT '商品名称',
  `commodityDescription` varchar(2048) DEFAULT NULL COMMENT '商品简介',
  `commodityAvatar` varchar(1024) DEFAULT NULL COMMENT '商品封面图',
  `degree` varchar(255) DEFAULT NULL COMMENT '商品新旧程度（例如 9成新）',
  `commodityTypeId` bigint(20) DEFAULT NULL COMMENT '商品分类 ID',
  `adminId` bigint(20) NOT NULL COMMENT '管理员 ID （某人创建该商品）',
  `isListed` tinyint(4) DEFAULT '0' COMMENT '是否上架（默认0未上架，1已上架）',
  `commodityInventory` int(11) DEFAULT '0' COMMENT '商品数量（默认0）',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `viewNum` int(11) DEFAULT '0' COMMENT '商品浏览量',
  `favourNum` int(11) DEFAULT '0' COMMENT '商品收藏量',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1900439195794169858 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品';

-- 评论表
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1966828939087572994 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='评论';
