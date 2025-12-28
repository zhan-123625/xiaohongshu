import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, OneToMany } from 'typeorm';
import { Note } from './note.entity';
import { Comment } from './comment.entity';

@Entity('users')
export class User {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id: number;

  @Column({ length: 50 })
  username: string;

  @Column({ length: 20, unique: true })
  phone: string;

  @Column({ name: 'password_hash' })
  passwordHash: string;

  @Column({ name: 'avatar_url', nullable: true })
  avatarUrl: string;

  @Column({ nullable: true })
  bio: string;

  @Column({ type: 'tinyint', default: 0 })
  gender: number; // 0:未知, 1:男, 2:女

  @Column({ name: 'following_count', type: 'int', default: 0 })
  followingCount: number;

  @Column({ name: 'follower_count', type: 'int', default: 0 })
  followerCount: number;

  @Column({ name: 'like_count', type: 'int', default: 0 })
  likeCount: number;

  @Column({ type: 'tinyint', default: 0 })
  role: number; // 0:普通用户, 1:管理员

  @Column({ type: 'tinyint', default: 1 })
  status: number; // 1:正常, 0:封禁

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @OneToMany(() => Note, (note) => note.user)
  notes: Note[];

  @OneToMany(() => Comment, (comment) => comment.user)
  comments: Comment[];
}
