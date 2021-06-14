/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : gulimall_wms

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2021-05-28 01:30:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint(20) DEFAULT NULL,
  `assignee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `priority` int(4) DEFAULT NULL,
  `status` int(4) DEFAULT NULL,
  `ware_id` bigint(20) DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购信息';

-- ----------------------------
-- Records of wms_purchase
-- ----------------------------
INSERT INTO `wms_purchase` VALUES ('1', '2', 'fireflynay', '18156475879', '1', '3', '1', '149700.0000', '2020-06-07 00:34:32', '2020-06-07 15:55:06');
INSERT INTO `wms_purchase` VALUES ('2', '1', 'admin', '18173516309', '1', '3', '1', '177760.0000', '2020-06-07 00:55:43', '2020-06-07 14:14:47');
INSERT INTO `wms_purchase` VALUES ('3', '1', 'admin', '18173516309', '1', '3', '1', '297520.0000', '2020-06-07 13:33:08', '2020-06-07 15:21:43');
INSERT INTO `wms_purchase` VALUES ('4', '2', 'fireflynay', '18156475879', '1', '3', '1', '179640.0000', '2020-06-07 14:01:10', '2020-06-07 15:18:35');

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `status` int(11) DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of wms_purchase_detail
-- ----------------------------
INSERT INTO `wms_purchase_detail` VALUES ('1', '1', '2', '10', '88880.0000', '1', '3');
INSERT INTO `wms_purchase_detail` VALUES ('2', '3', '2', '20', '177760.0000', '1', '3');
INSERT INTO `wms_purchase_detail` VALUES ('3', '3', '3', '5', '29940.0000', '1', '3');
INSERT INTO `wms_purchase_detail` VALUES ('4', '3', '3', '15', '89820.0000', '1', '3');
INSERT INTO `wms_purchase_detail` VALUES ('5', '4', '4', '30', '179640.0000', '1', '3');
INSERT INTO `wms_purchase_detail` VALUES ('6', '1', '5', '25', '149700.0000', '1', '3');

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='仓库信息';

-- ----------------------------
-- Records of wms_ware_info
-- ----------------------------
INSERT INTO `wms_ware_info` VALUES ('1', '1号仓库', '长沙市', '410000');

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存工作单';

-- ----------------------------
-- Records of wms_ware_order_task
-- ----------------------------
INSERT INTO `wms_ware_order_task` VALUES ('1', null, '202007101511435951281486241929375746', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `wms_ware_order_task` VALUES ('2', null, '202007102108315951281576033585246209', null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `lock_status` int(1) DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存工作单';

-- ----------------------------
-- Records of wms_ware_order_task_detail
-- ----------------------------
INSERT INTO `wms_ware_order_task_detail` VALUES ('1', '2', '', '1', '1', '1', '2');
INSERT INTO `wms_ware_order_task_detail` VALUES ('2', '2', '', '1', '2', '1', '2');

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) DEFAULT '0' COMMENT '锁定库存',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sku_id` (`sku_id`) USING BTREE,
  KEY `ware_id` (`ware_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商品库存';

-- ----------------------------
-- Records of wms_ware_sku
-- ----------------------------
INSERT INTO `wms_ware_sku` VALUES ('3', '2', '1', '50', '华为 HUAWEI P40 Pro+ 麒麟990 5G  流光幻镜 套餐三', '0');
INSERT INTO `wms_ware_sku` VALUES ('4', '3', '1', '35', '华为 HUAWEI P40 Pro+ 麒麟990 5G  流光幻镜 套餐一', '0');
INSERT INTO `wms_ware_sku` VALUES ('5', '4', '1', '60', '华为 HUAWEI P40 Pro+ 麒麟990 5G  霓影紫 套餐二', '0');
INSERT INTO `wms_ware_sku` VALUES ('6', '5', '1', '125', '华为 HUAWEI P40 Pro+ 麒麟990 5G  霓影紫 套餐三', '0');
