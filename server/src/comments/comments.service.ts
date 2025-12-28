import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Comment } from '../entities/comment.entity';

@Injectable()
export class CommentsService {
  private readonly logger = new Logger(CommentsService.name);

  constructor(
    @InjectRepository(Comment)
    private commentsRepository: Repository<Comment>,
  ) {}

  async create(commentData: Partial<Comment>): Promise<Comment> {
    this.logger.log(`create: commentData=${JSON.stringify(commentData)}`);
    const comment = this.commentsRepository.create(commentData);
    const savedComment = await this.commentsRepository.save(comment);
    this.logger.log(`create: savedCommentId=${savedComment.id}`);
    return this.commentsRepository.findOne({
      where: { id: savedComment.id },
      relations: ['user'],
    });
  }

  async findByNoteId(noteId: number): Promise<Comment[]> {
    this.logger.log(`findByNoteId: noteId=${noteId}`);
    const comments = await this.commentsRepository.find({
      where: { noteId },
      order: { createdAt: 'ASC' },
      relations: ['user'],
    });
    this.logger.log(`findByNoteId: found ${comments.length} comments`);
    return comments;
  }
}
