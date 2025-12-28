-- Create Database
CREATE DATABASE IF NOT EXISTS xiaohongshu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiaohongshu;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像链接',
  `bio` VARCHAR(255) DEFAULT NULL COMMENT '个人简介',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0:未知 1:男 2:女',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. Notes Table
CREATE TABLE IF NOT EXISTS `notes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者ID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '正文内容',
  `type` TINYINT DEFAULT 1 COMMENT '类型 1:图文 2:视频',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图链接',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '地理位置',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0:草稿 1:发布 2:审核中 3:违规',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_notes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';

-- 3. Note Resources Table (Images/Videos)
CREATE TABLE IF NOT EXISTS `note_resources` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `note_id` BIGINT NOT NULL COMMENT '关联笔记ID',
  `url` VARCHAR(255) NOT NULL COMMENT '资源链接',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `type` TINYINT DEFAULT 1 COMMENT '资源类型 1:图片 2:视频',
  PRIMARY KEY (`id`),
  KEY `idx_note_id` (`note_id`),
  CONSTRAINT `fk_resources_note` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记资源表';

-- 4. Comments Table
CREATE TABLE IF NOT EXISTS `comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `note_id` BIGINT NOT NULL COMMENT '关联笔记ID',
  `user_id` BIGINT NOT NULL COMMENT '评论者ID',
  `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_note_id` (`note_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comments_note` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 5. Likes Table (Polymorphic for Notes and Comments if needed, currently simplified for Notes/Comments via type)
CREATE TABLE IF NOT EXISTS `likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_id` BIGINT NOT NULL COMMENT '目标ID (笔记ID或评论ID)',
  `type` TINYINT NOT NULL COMMENT '类型 1:笔记 2:评论',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target_type` (`user_id`, `target_id`, `type`),
  CONSTRAINT `fk_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 6. Collections Table
CREATE TABLE IF NOT EXISTS `collections` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `note_id` BIGINT NOT NULL COMMENT '笔记ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_note` (`user_id`, `note_id`),
  CONSTRAINT `fk_collections_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_collections_note` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 7. Follows Table
CREATE TABLE IF NOT EXISTS `follows` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` BIGINT NOT NULL COMMENT '粉丝ID',
  `following_id` BIGINT NOT NULL COMMENT '被关注者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
  CONSTRAINT `fk_follows_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follows_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';
