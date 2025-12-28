import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from '../entities/user.entity';
import { Note } from '../entities/note.entity';
import { Comment } from '../entities/comment.entity';
import { AdminLog } from '../entities/admin-log.entity';

@Injectable()
export class AdminService {
  constructor(
    @InjectRepository(User)
    private usersRepository: Repository<User>,
    @InjectRepository(Note)
    private notesRepository: Repository<Note>,
    @InjectRepository(Comment)
    private commentsRepository: Repository<Comment>,
    @InjectRepository(AdminLog)
    private adminLogsRepository: Repository<AdminLog>,
  ) {}

  async getStats() {
    const userCount = await this.usersRepository.count();
    const noteCount = await this.notesRepository.count();
    const pendingNotes = await this.notesRepository.count({ where: { status: 2 } });
    return { userCount, noteCount, pendingNotes };
  }

  async getUsers(page: number = 1, limit: number = 10) {
    return this.usersRepository.find({
      take: limit,
      skip: (page - 1) * limit,
      order: { createdAt: 'DESC' },
    });
  }

  async updateUserStatus(adminId: number, userId: number, status: number, ip: string) {
    await this.usersRepository.update(userId, { status });
    await this.logAction(adminId, 'UPDATE_USER_STATUS', userId, `Status changed to ${status}`, ip);
    return { success: true };
  }

  async getNotes(page: number = 1, limit: number = 10, status?: number) {
    const where = status !== undefined ? { status } : {};
    return this.notesRepository.find({
      where,
      take: limit,
      skip: (page - 1) * limit,
      order: { createdAt: 'DESC' },
      relations: ['user'],
    });
  }

  async auditNote(adminId: number, noteId: number, status: number, reason: string, ip: string) {
    await this.notesRepository.update(noteId, { status });
    await this.logAction(adminId, 'AUDIT_NOTE', noteId, `Status: ${status}, Reason: ${reason}`, ip);
    return { success: true };
  }

  async deleteComment(adminId: number, commentId: number, ip: string) {
    await this.commentsRepository.delete(commentId);
    await this.logAction(adminId, 'DELETE_COMMENT', commentId, '', ip);
    return { success: true };
  }

  private async logAction(adminId: number, action: string, targetId: number, details: string, ip: string) {
    const log = this.adminLogsRepository.create({
      adminId,
      action,
      targetId,
      details,
      ipAddress: ip,
    });
    await this.adminLogsRepository.save(log);
  }
}
