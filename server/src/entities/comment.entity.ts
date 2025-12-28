import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, ManyToOne, JoinColumn } from 'typeorm';
import { User } from './user.entity';
import { Note } from './note.entity';

@Entity('comments')
export class Comment {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id: number;

  @Column({ name: 'note_id', type: 'bigint' })
  noteId: number;

  @ManyToOne(() => Note)
  @JoinColumn({ name: 'note_id' })
  note: Note;

  @Column({ name: 'user_id', type: 'bigint' })
  userId: number;

  @ManyToOne(() => User)
  @JoinColumn({ name: 'user_id' })
  user: User;

  @Column({ length: 1000 })
  content: string;

  @Column({ name: 'parent_id', type: 'bigint', nullable: true })
  parentId: number;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;
}
