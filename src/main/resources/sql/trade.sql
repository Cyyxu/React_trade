/*
 Navicat Premium Dump SQL

 Source Server         : 120.26.104.183_3306
 Source Server Type    : MySQL
 Source Server Version : 50743 (5.7.43-log)
 Source Host           : 120.26.104.183:3306
 Source Schema         : trade

 Target Server Type    : MySQL
 Target Server Version : 50743 (5.7.43-log)
 File Encoding         : 65001

 Date: 27/11/2025 11:13:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='评论';

-- ----------------------------
-- Records of comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6141462386511873 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品';

-- ----------------------------
-- Records of commodity
-- ----------------------------
BEGIN;
INSERT INTO `commodity` (`id`, `commodityName`, `commodityDescription`, `commodityAvatar`, `degree`, `commodityTypeId`, `adminId`, `isListed`, `commodityInventory`, `price`, `viewNum`, `favourNum`, `createTime`, `updateTime`, `isDelete`) VALUES (6141462386511872, '取消出去；', '气温持续不凑巧', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.2gMA25FGJssIRNynRsIUCAHaJ4?rs=1&pid=ImgDetMain&o=7&rm=3', '9成新', 2, 6141432151478272, 1, 9, 100.00, 0, 0, '2025-11-26 16:13:04', '2025-11-26 19:27:02', 0);
COMMIT;

-- ----------------------------
-- Table structure for commodity_order
-- ----------------------------
DROP TABLE IF EXISTS `commodity_order`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6143270625677313 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品订单';

-- ----------------------------
-- Records of commodity_order
-- ----------------------------
BEGIN;
INSERT INTO `commodity_order` (`id`, `userId`, `commodityId`, `remark`, `buyNumber`, `paymentAmount`, `payStatus`, `createTime`, `updateTime`, `isDelete`) VALUES (6143270625677312, 6141432151478272, 6141462386511872, NULL, 1, 100.00, 1, '2025-11-26 20:03:00', '2025-11-26 20:03:00', 0);
COMMIT;

-- ----------------------------
-- Table structure for commodity_score
-- ----------------------------
DROP TABLE IF EXISTS `commodity_score`;
CREATE TABLE `commodity_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品评分 ID',
  `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
  `userId` bigint(20) NOT NULL COMMENT '用户 ID',
  `score` int(11) NOT NULL COMMENT '评分（0-5，星级评分）',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6142990126354433 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of commodity_score
-- ----------------------------
BEGIN;
INSERT INTO `commodity_score` (`id`, `commodityId`, `userId`, `score`, `createTime`, `updateTime`, `isDelete`) VALUES (6142990126354432, 6141462386511872, 6141432151478272, 5, '2025-11-26 19:27:20', '2025-11-26 19:27:26', 0);
COMMIT;

-- ----------------------------
-- Table structure for commodity_type
-- ----------------------------
DROP TABLE IF EXISTS `commodity_type`;
CREATE TABLE `commodity_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品分类 ID',
  `typeName` varchar(255) NOT NULL COMMENT '商品类别名称',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品分类';

-- ----------------------------
-- Records of commodity_type
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `noticeTitle` varchar(255) NOT NULL COMMENT '公告标题',
  `noticeContent` varchar(255) NOT NULL COMMENT '公告内容',
  `noticeAdminId` bigint(20) NOT NULL COMMENT '创建人id（管理员）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公告';

-- ----------------------------
-- Records of notice
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6141457398435841 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='帖子';

-- ----------------------------
-- Records of post
-- ----------------------------
BEGIN;
INSERT INTO `post` (`id`, `title`, `content`, `tags`, `thumbNum`, `favourNum`, `userId`, `createTime`, `updateTime`, `isDelete`) VALUES (6141457398435840, 'cfuiwvb2', 'cqcbocbnivbi不', '[]', 1, 1, 6141432151478272, '2025-11-26 16:12:26', '2025-11-26 20:01:25', 0);
COMMIT;

-- ----------------------------
-- Table structure for post_favour
-- ----------------------------
DROP TABLE IF EXISTS `post_favour`;
CREATE TABLE `post_favour` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6143258098995201 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帖子收藏';

-- ----------------------------
-- Records of post_favour
-- ----------------------------
BEGIN;
INSERT INTO `post_favour` (`id`, `postId`, `userId`, `createTime`, `updateTime`) VALUES (6143258098995200, 6141457398435840, 6141432151478272, '2025-11-26 20:01:25', '2025-11-26 20:01:25');
COMMIT;

-- ----------------------------
-- Table structure for post_thumb
-- ----------------------------
DROP TABLE IF EXISTS `post_thumb`;
CREATE TABLE `post_thumb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6143257969364993 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帖子点赞';

-- ----------------------------
-- Records of post_thumb
-- ----------------------------
BEGIN;
INSERT INTO `post_thumb` (`id`, `postId`, `userId`, `createTime`, `updateTime`) VALUES (6143257969364992, 6141457398435840, 6141432151478272, '2025-11-26 20:01:24', '2025-11-26 20:01:24');
COMMIT;

-- ----------------------------
-- Table structure for private_message
-- ----------------------------
DROP TABLE IF EXISTS `private_message`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='私聊消息';

-- ----------------------------
-- Records of private_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for user_ai_message
-- ----------------------------
DROP TABLE IF EXISTS `user_ai_message`;
CREATE TABLE `user_ai_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userInputText` varchar(4096) NOT NULL COMMENT '用户输入',
  `aiGenerateText` text COMMENT 'AI生成的文本',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6141716552421377 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户AI消息';

-- ----------------------------
-- Records of user_ai_message
-- ----------------------------
BEGIN;
INSERT INTO `user_ai_message` (`id`, `userInputText`, `aiGenerateText`, `userId`, `createTime`, `updateTime`, `isDelete`) VALUES (6141716552421376, 'nh', '好的，请您提供以下信息，我才能更好地为您推荐二手商品：\n\n1.  **用户的偏好信息 (nh):** 请详细描述用户的偏好，包括：\n    *   **感兴趣的商品类型:** 例如：书籍、电子产品、服装、家具、运动器材、母婴用品等。\n    *   **价格范围:** 用户能够接受的最高和最低价格。\n    *   **对新旧程度的偏好:** 例如：喜欢9成新以上的，或者可以接受有轻微瑕疵的。\n    *   **品牌偏好:** 是否有特别喜欢的品牌或避免的品牌。\n    *   **其他特殊需求:** 例如：特定尺寸、颜色、功能等。\n2.  **数据库商品信息:** 请提供数据库中商品的详细信息，格式为：`商品名|新旧程度|库存|价格`，并确保每条信息之间用换行符分隔。 例如：\n\n    ```\n    Kindle Paperwhite|9成新|5|500\n    李维斯牛仔裤|8成新|2|150\n    宜家书桌|7成新|1|300\n    小米手环5|全新|10|180\n    宝宝玩具套装|6成新|3|80\n    ```\n\n有了这些信息，我就可以开始分析并给出推荐理由了。 例如，我会说：\n\n\"根据您对电子产品、价格在500元左右、且偏好9成新以上的Kindle的偏好，我推荐这款Kindle Paperwhite。它有9成新，库存充足，价格也符合您的预算。Kindle非常适合喜欢阅读的您，可以随时随地享受阅读的乐趣。\"\n\n请您尽快提供以上信息，我将竭诚为您服务！\n', 6141432151478272, '2025-11-26 16:45:24', '2025-11-26 16:45:24', 0);
COMMIT;

-- ----------------------------
-- Table structure for user_commodity_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_commodity_favorite`;
CREATE TABLE `user_commodity_favorite` (
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
) ENGINE=InnoDB AUTO_INCREMENT=6142991524630529 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user_commodity_favorite
-- ----------------------------
BEGIN;
INSERT INTO `user_commodity_favorite` (`id`, `userId`, `commodityId`, `status`, `remark`, `createTime`, `updateTime`, `isDelete`) VALUES (6142991524630528, 6141432151478272, 6141462386511872, 'active', NULL, '2025-11-26 19:27:31', '2025-11-27 09:48:53', 1);
COMMIT;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6149861510025217 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户';

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` (`id`, `userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userPhone`, `userEmail`, `userSchool`, `userMajor`, `userAddress`, `editTime`, `createTime`, `updateTime`, `isDelete`) VALUES (6141254320066560, 'aqswq', '25d55ad283aa400af464c76d713c07ad', NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, '2025-11-26 15:46:46', '2025-11-26 15:46:46', '2025-11-26 15:46:46', 0);
INSERT INTO `user_info` (`id`, `userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userPhone`, `userEmail`, `userSchool`, `userMajor`, `userAddress`, `editTime`, `createTime`, `updateTime`, `isDelete`) VALUES (6141370460475392, 'qwqc', '25d55ad283aa400af464c76d713c07ad', NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, '2025-11-26 16:01:23', '2025-11-26 16:01:23', '2025-11-26 16:01:23', 0);
INSERT INTO `user_info` (`id`, `userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userPhone`, `userEmail`, `userSchool`, `userMajor`, `userAddress`, `editTime`, `createTime`, `updateTime`, `isDelete`) VALUES (6141432151478272, 'adminxj', '25d55ad283aa400af464c76d713c07ad', NULL, NULL, NULL, NULL, NULL, 'admin', NULL, NULL, NULL, NULL, NULL, '2025-11-26 16:09:14', '2025-11-26 16:09:14', '2025-11-26 16:53:53', 0);
INSERT INTO `user_info` (`id`, `userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userPhone`, `userEmail`, `userSchool`, `userMajor`, `userAddress`, `editTime`, `createTime`, `updateTime`, `isDelete`) VALUES (6149776587165696, 'xjanwn', '25d55ad283aa400af464c76d713c07ad', NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, '2025-11-27 09:50:17', '2025-11-27 09:50:17', '2025-11-27 09:50:17', 0);
INSERT INTO `user_info` (`id`, `userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userPhone`, `userEmail`, `userSchool`, `userMajor`, `userAddress`, `editTime`, `createTime`, `updateTime`, `isDelete`) VALUES (6149861510025216, 'sqdpin', '25d55ad283aa400af464c76d713c07ad', NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, '2025-11-27 10:01:05', '2025-11-27 10:01:05', '2025-11-27 10:01:05', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
