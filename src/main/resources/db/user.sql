-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name`   CHAR(100) NOT NULL,
    `height` REAL      NOT NULL DEFAULT '0',
    `intro`  TEXT               DEFAULT NULL
);
