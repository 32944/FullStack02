-- 用户收藏题目表 - 多对多关系
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `problem_id` BIGINT NOT NULL COMMENT '题目ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`user_id`, `problem_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏题目表';

-- 用户错题表
CREATE TABLE IF NOT EXISTS `user_wrong` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `problem_id` BIGINT NOT NULL COMMENT '题目ID',
  `wrong_count` INT NOT NULL DEFAULT 1 COMMENT '错误次数',
  `last_wrong_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后错误时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`, `problem_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户错题表';
