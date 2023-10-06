/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : overlap

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 05/10/2023 11:18:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`  (
  `id` bigint(0) NOT NULL,
  `side1` bigint(0) NULL DEFAULT NULL,
  `side2` bigint(0) NULL DEFAULT NULL,
  `type` tinyint(0) NULL DEFAULT 0 COMMENT '0：单聊；1：群聊',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `is_delete` tinyint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `side1`(`side1`) USING BTREE,
  INDEX `side2`(`side2`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coterie
-- ----------------------------
DROP TABLE IF EXISTS `coterie`;
CREATE TABLE `coterie`  (
  `id` bigint(0) NOT NULL,
  `coterie_num` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '圈号',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_encrypted` tinyint(0) NULL DEFAULT NULL COMMENT '是否加密',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '圈子状态，0：公开；1：私密',
  `max_num` int(0) NULL DEFAULT NULL COMMENT '最大人数限制',
  `cur_num` int(0) NULL DEFAULT NULL COMMENT '当前人数',
  `is_delete` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `coterieNum`(`coterie_num`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `coterie_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint(0) NOT NULL,
  `from_id` bigint(0) NULL DEFAULT NULL COMMENT '发送方',
  `to_id` bigint(0) NULL DEFAULT NULL COMMENT '接收方',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `coterie_id` bigint(0) NULL DEFAULT NULL COMMENT '消息所属圈子',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '在单聊中是对方头像路径；在群聊中是己方头像路径',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送内容',
  `type` tinyint(0) NULL DEFAULT 0 COMMENT '0：文本',
  `is_read` tinyint(0) NULL DEFAULT 0 COMMENT '0：未读；1：已读',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(0) NULL DEFAULT 0 COMMENT '0：未删除；1：逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_ibfk_1`(`from_id`) USING BTREE,
  INDEX `to_id`(`to_id`) USING BTREE,
  INDEX `coterie_id`(`coterie_id`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`from_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`to_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_3` FOREIGN KEY (`coterie_id`) REFERENCES `coterie` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父标签（属于哪一行）',
  `classify_id` bigint(0) NULL DEFAULT NULL COMMENT '分类标签（属于哪一列）',
  `is_parent` tinyint(0) NULL DEFAULT NULL COMMENT '0：不是父标签；1：是父标签',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `is_delete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0: 未删除；1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tag`(`tag_name`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE,
  INDEX `classify_id`(`classify_id`) USING BTREE,
  CONSTRAINT `tag_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tag_ibfk_4` FOREIGN KEY (`parent_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tag_ibfk_5` FOREIGN KEY (`classify_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '游戏', 1, NULL, NULL, 1, '2023-08-05 11:07:34', '2023-08-05 11:07:34', 0);
INSERT INTO `tag` VALUES (2, '王者荣耀', 1, 1, NULL, 0, '2023-08-05 11:07:49', '2023-08-05 11:07:49', 0);
INSERT INTO `tag` VALUES (3, '蛋仔派对', 1, 1, NULL, 0, '2023-08-05 11:08:05', '2023-08-05 11:08:05', 0);
INSERT INTO `tag` VALUES (4, '段位', 1, NULL, NULL, 1, '2023-08-05 11:08:32', '2023-08-10 11:46:03', 0);
INSERT INTO `tag` VALUES (5, '青铜', 1, 4, 2, 0, '2023-08-05 11:09:05', '2023-08-05 11:09:05', 0);
INSERT INTO `tag` VALUES (6, '白银', 1, 4, 2, 0, '2023-08-05 11:09:24', '2023-08-05 11:09:24', 0);
INSERT INTO `tag` VALUES (7, '黄金', 1, 4, 2, 0, '2023-08-05 11:09:45', '2023-08-05 11:09:45', 0);
INSERT INTO `tag` VALUES (8, '铂金', 1, 4, 2, 0, '2023-08-05 11:10:02', '2023-08-05 11:10:02', 0);
INSERT INTO `tag` VALUES (9, '钻石', 1, 4, 2, 0, '2023-08-05 11:10:17', '2023-08-05 11:10:17', 0);
INSERT INTO `tag` VALUES (10, '星耀', 1, 4, 2, 0, '2023-08-10 22:28:08', '2023-08-10 22:29:18', 0);
INSERT INTO `tag` VALUES (11, '最强王者', 1, 4, 2, 0, '2023-08-05 11:10:30', '2023-08-05 11:10:38', 0);
INSERT INTO `tag` VALUES (12, '无双王者', 1, 4, 2, 0, '2023-08-05 11:10:51', '2023-08-05 11:10:51', 0);
INSERT INTO `tag` VALUES (13, '荣耀王者', 1, 4, 2, 0, '2023-08-05 11:11:05', '2023-08-05 11:11:05', 0);
INSERT INTO `tag` VALUES (14, '传奇王者', 1, 4, 2, 0, '2023-08-05 11:11:56', '2023-08-05 11:11:56', 0);
INSERT INTO `tag` VALUES (15, '鹌鹑蛋', 1, 4, 3, 0, '2023-08-05 11:13:37', '2023-08-05 11:13:37', 0);
INSERT INTO `tag` VALUES (16, '鸽子蛋', 1, 4, 3, 0, '2023-08-05 11:13:50', '2023-08-05 11:13:50', 0);
INSERT INTO `tag` VALUES (17, '鸡蛋', 1, 4, 3, 0, '2023-08-05 11:14:00', '2023-08-05 11:14:00', 0);
INSERT INTO `tag` VALUES (18, '鹅蛋', 1, 4, 3, 0, '2023-08-05 11:14:12', '2023-08-05 11:14:12', 0);
INSERT INTO `tag` VALUES (19, '鸵鸟蛋', 1, 4, 3, 0, '2023-08-05 11:14:23', '2023-08-05 11:14:23', 0);
INSERT INTO `tag` VALUES (20, '恐龙蛋', 1, 4, 3, 0, '2023-08-05 11:14:33', '2023-08-05 11:14:33', 0);
INSERT INTO `tag` VALUES (21, '巅峰凤凰蛋', 1, 4, 3, 0, '2023-08-05 11:14:49', '2023-08-05 11:14:49', 0);
INSERT INTO `tag` VALUES (22, '性别', 1, NULL, NULL, 1, '2023-08-05 11:17:23', '2023-08-05 11:17:40', 0);
INSERT INTO `tag` VALUES (23, '男', 1, 22, NULL, 0, '2023-08-05 11:18:15', '2023-08-10 22:38:16', 0);
INSERT INTO `tag` VALUES (24, '女', 1, 22, NULL, 0, '2023-08-05 11:18:33', '2023-08-10 22:38:18', 0);
INSERT INTO `tag` VALUES (25, '年级', 1, NULL, NULL, 1, '2023-08-05 11:19:03', '2023-08-05 11:19:03', 0);
INSERT INTO `tag` VALUES (26, '大一', 1, 25, NULL, 0, '2023-08-05 11:19:24', '2023-08-05 11:19:24', 0);
INSERT INTO `tag` VALUES (27, '大二', 1, 25, NULL, 0, '2023-08-05 11:19:43', '2023-08-05 11:19:43', 0);
INSERT INTO `tag` VALUES (28, '大三', 1, 25, NULL, 0, '2023-08-05 11:19:52', '2023-08-05 11:19:52', 0);
INSERT INTO `tag` VALUES (29, '大四', 1, 25, NULL, 0, '2023-08-05 11:20:05', '2023-08-05 11:20:05', 0);
INSERT INTO `tag` VALUES (30, '已毕业', 1, 25, NULL, 0, '2023-08-05 11:20:19', '2023-08-05 11:20:19', 0);
INSERT INTO `tag` VALUES (31, '方向', 1, NULL, NULL, 1, '2023-08-05 11:21:38', '2023-08-05 11:21:38', 0);
INSERT INTO `tag` VALUES (32, '恋爱', 1, 31, NULL, 0, '2023-08-05 11:22:05', '2023-08-05 11:23:22', 0);
INSERT INTO `tag` VALUES (33, '饭搭子', 1, 31, NULL, 0, '2023-08-05 11:22:20', '2023-08-05 11:22:20', 0);
INSERT INTO `tag` VALUES (34, '学习搭子', 1, 31, NULL, 0, '2023-08-05 11:22:31', '2023-08-05 11:22:31', 0);
INSERT INTO `tag` VALUES (35, '游戏搭子', 1, 31, NULL, 0, '2023-08-05 11:22:44', '2023-08-05 11:22:44', 0);
INSERT INTO `tag` VALUES (36, '聊天搭子', 1, 31, NULL, 0, '2023-08-05 11:22:56', '2023-08-05 11:22:56', 0);
INSERT INTO `tag` VALUES (37, '状态', 1, NULL, NULL, 1, '2023-08-05 11:23:35', '2023-08-05 11:23:35', 0);
INSERT INTO `tag` VALUES (38, '不摆烂', 1, 37, NULL, 0, '2023-08-05 11:23:54', '2023-08-05 11:23:54', 0);
INSERT INTO `tag` VALUES (39, 'emo', 1, 37, NULL, 0, '2023-08-05 11:24:08', '2023-08-05 11:24:08', 0);
INSERT INTO `tag` VALUES (40, '开麦', 1, 37, NULL, 0, '2023-08-05 11:24:21', '2023-08-05 11:24:21', 0);
INSERT INTO `tag` VALUES (41, '不骂人', 1, 37, NULL, 0, '2023-08-05 11:24:33', '2023-08-05 11:24:33', 0);
INSERT INTO `tag` VALUES (42, '积极', 1, 37, NULL, 0, '2023-08-05 11:24:48', '2023-08-05 11:24:48', 0);
INSERT INTO `tag` VALUES (43, '夜猫子', 1, 37, NULL, 0, '2023-08-10 16:03:58', '2023-08-10 16:03:58', 0);
INSERT INTO `tag` VALUES (44, '佛系', 1, 37, NULL, 0, '2023-08-10 16:08:40', '2023-08-10 16:08:40', 0);
INSERT INTO `tag` VALUES (45, '中二', 1, 37, NULL, 0, '2023-08-10 16:09:56', '2023-08-10 16:09:56', 0);
INSERT INTO `tag` VALUES (46, '声控', 1, 37, NULL, 0, '2023-08-10 16:10:47', '2023-08-10 16:10:47', 0);
INSERT INTO `tag` VALUES (47, '慢热', 1, 37, NULL, 0, '2023-08-10 16:10:59', '2023-08-10 16:10:59', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL,
  `user_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `age` int(0) NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_post` tinyint(0) NULL DEFAULT 0 COMMENT '1: 发帖；0：未发帖',
  `user_status` tinyint(0) NULL DEFAULT 0 COMMENT '0-正常',
  `user_role` tinyint(0) NULL DEFAULT 0 COMMENT '0-普通用户；1-管理员',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签列表，用 json 存储',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `is_delete` tinyint(0) NULL DEFAULT 0 COMMENT '1:已经删除；0：未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ovta', '36ab334e9b37f04d6730db63a32d8d1e', '懒大王', 20, '男', '爱吃爱睡', 'http://easyimage.ovta.love/i/2023/08/16/tthsx8.webp', '18870754369', '3204864983@qq.com', 1, 0, 1, '[\"球球大作战\",\"巅峰凤凰蛋\",\"无敌恐龙蛋\",\"传奇王者蛋\",\"二进制恋爱\",\"蛋仔派对\"]', '2023-08-02 20:34:08', '2023-09-04 20:43:40', 0);

-- ----------------------------
-- Table structure for user_coterie
-- ----------------------------
DROP TABLE IF EXISTS `user_coterie`;
CREATE TABLE `user_coterie`  (
  `id` bigint(0) NOT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `coterie_id` bigint(0) NULL DEFAULT NULL,
  `role` tinyint(0) NULL DEFAULT 0 COMMENT '1: 圈主；0: 普通',
  `join_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `userId`(`user_id`) USING BTREE,
  INDEX `coterieId`(`coterie_id`) USING BTREE,
  CONSTRAINT `coterieId` FOREIGN KEY (`coterie_id`) REFERENCES `coterie` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `userId` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
