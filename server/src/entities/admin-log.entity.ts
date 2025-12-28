import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn } from 'typeorm';

@Entity('admin_logs')
export class AdminLog {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id: number;

  @Column({ name: 'admin_id', type: 'bigint' })
  adminId: number;

  @Column({ length: 50 })
  action: string;

  @Column({ name: 'target_id', type: 'bigint', nullable: true })
  targetId: number;

  @Column({ type: 'text', nullable: true })
  details: string;

  @Column({ name: 'ip_address', length: 45, nullable: true })
  ipAddress: string;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;
}
