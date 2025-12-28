import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, ManyToOne, JoinColumn, OneToMany } from 'typeorm';
import { User } from './user.entity';
import { Comment } from './comment.entity';

@Entity('notes')
export class Note {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id: number;

  @Column({ name: 'user_id', type: 'bigint' })
  userId: number;

  @ManyToOne(() => User, (user) => user.notes)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @Column({ length: 100 })
  title: string;

  @Column({ type: 'text' })
  content: string;

  @Column({ type: 'tinyint', default: 1 })
  type: number; // 1:图文, 2:视频

  @Column({ name: 'cover_url', nullable: true })
  coverUrl: string;

  @Column({ nullable: true })
  location: string;

  @Column({ type: 'tinyint', default: 1 })
  status: number; // 0:草稿, 1:发布, 2:审核中, 3:违规

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @OneToMany(() => Comment, (comment) => comment.note)
  comments: Comment[];
}
