import { Injectable, Logger, NotFoundException, ForbiddenException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, In } from 'typeorm';
import { Note } from '../entities/note.entity';
import { Like } from '../entities/like.entity';
import { Collection } from '../entities/collection.entity';
import { Follow } from '../entities/follow.entity';

@Injectable()
export class NotesService {
  private readonly logger = new Logger(NotesService.name);

  constructor(
    @InjectRepository(Note)
    private notesRepository: Repository<Note>,
    @InjectRepository(Like)
    private likesRepository: Repository<Like>,
    @InjectRepository(Collection)
    private collectionsRepository: Repository<Collection>,
    @InjectRepository(Follow)
    private followsRepository: Repository<Follow>,
  ) {}

  async findAll(page: number = 1, limit: number = 10, currentUserId?: number): Promise<any[]> {
    this.logger.log(`findAll: page=${page}, limit=${limit}, currentUserId=${currentUserId}`);
    const notes = await this.notesRepository.find({
      take: limit,
      skip: (page - 1) * limit,
      order: { createdAt: 'DESC' },
      relations: ['user'],
    });

    return Promise.all(notes.map(async (note) => {
      return this.enrichNote(note, currentUserId);
    }));
  }

  async findOne(id: number, currentUserId?: number): Promise<any> {
    this.logger.log(`findOne: id=${id}, currentUserId=${currentUserId}`);
    const note = await this.notesRepository.findOne({ where: { id }, relations: ['user'] });
    if (!note) {
      this.logger.warn(`findOne: Note not found id=${id}`);
      return null;
    }
    return this.enrichNote(note, currentUserId);
  }

  private async enrichNote(note: Note, currentUserId?: number) {
    const likeCount = await this.likesRepository.count({ where: { targetId: note.id, type: 1 } });
    const collectionCount = await this.collectionsRepository.count({ where: { noteId: note.id } });
    // const commentCount = await this.commentsRepository.count({ where: { noteId: note.id } }); // Need to inject CommentsRepository

    let isLiked = false;
    let isCollected = false;
    let isFollowing = false;

    if (currentUserId) {
      isLiked = await this.likesRepository.count({ where: { targetId: note.id, type: 1, userId: currentUserId } }) > 0;
      isCollected = await this.collectionsRepository.count({ where: { noteId: note.id, userId: currentUserId } }) > 0;
      if (note.userId !== currentUserId) {
          isFollowing = await this.followsRepository.count({ where: { followerId: currentUserId, followingId: note.userId } }) > 0;
      }
    }

    return {
      ...note,
      likeCount,
      collectionCount,
      isLiked,
      isCollected,
      isFollowing,
    };
  }

  async toggleLike(userId: number, noteId: number): Promise<{ isLiked: boolean, count: number }> {
    this.logger.log(`toggleLike: userId=${userId}, noteId=${noteId}`);
    const existing = await this.likesRepository.findOne({ where: { userId, targetId: noteId, type: 1 } });
    if (existing) {
      await this.likesRepository.remove(existing);
      this.logger.log(`toggleLike: Removed like`);
    } else {
      const like = this.likesRepository.create({ userId, targetId: noteId, type: 1 });
      await this.likesRepository.save(like);
      this.logger.log(`toggleLike: Added like`);
    }
    const count = await this.likesRepository.count({ where: { targetId: noteId, type: 1 } });
    return { isLiked: !existing, count };
  }

  async toggleCollect(userId: number, noteId: number): Promise<{ isCollected: boolean, count: number }> {
    this.logger.log(`toggleCollect: userId=${userId}, noteId=${noteId}`);
    const existing = await this.collectionsRepository.findOne({ where: { userId, noteId } });
    if (existing) {
      await this.collectionsRepository.remove(existing);
      this.logger.log(`toggleCollect: Removed collection`);
    } else {
      const collection = this.collectionsRepository.create({ userId, noteId });
      await this.collectionsRepository.save(collection);
      this.logger.log(`toggleCollect: Added collection`);
    }
    const count = await this.collectionsRepository.count({ where: { noteId } });
    return { isCollected: !existing, count };
  }

  async create(noteData: Partial<Note>): Promise<Note> {
    this.logger.log(`create: noteData=${JSON.stringify(noteData)}`);
    const note = this.notesRepository.create(noteData);
    return this.notesRepository.save(note);
  }

  async delete(id: number, userId: number): Promise<void> {
    const note = await this.notesRepository.findOne({ where: { id }, relations: ['user'] });
    if (!note) {
      throw new NotFoundException('Note not found');
    }
    if (note.user.id !== userId) {
      throw new ForbiddenException('You can only delete your own notes');
    }
    await this.notesRepository.remove(note);
  }

  async findByUserId(userId: number): Promise<any[]> {
    this.logger.log(`findByUserId: userId=${userId}`);
    const notes = await this.notesRepository.find({
      where: { userId },
      order: { createdAt: 'DESC' },
      relations: ['user'],
    });
    return Promise.all(notes.map(async (note) => {
      return this.enrichNote(note, userId);
    }));
  }

  async findLikedByUserId(userId: number): Promise<any[]> {
    this.logger.log(`findLikedByUserId: userId=${userId}`);
    const likes = await this.likesRepository.find({ where: { userId, type: 1 }, order: { createdAt: 'DESC' } });
    const noteIds = likes.map(l => l.targetId);
    if (noteIds.length === 0) return [];
    
    const notes = await this.notesRepository.find({
        where: { id: In(noteIds) },
        relations: ['user']
    });

    return Promise.all(notes.map(async (note) => {
      return this.enrichNote(note, userId);
    }));
  }

  async findCollectedByUserId(userId: number): Promise<any[]> {
    this.logger.log(`findCollectedByUserId: userId=${userId}`);
    const collections = await this.collectionsRepository.find({ where: { userId }, order: { createdAt: 'DESC' } });
    const noteIds = collections.map(c => c.noteId);
    if (noteIds.length === 0) return [];

    const notes = await this.notesRepository.find({
        where: { id: In(noteIds) },
        relations: ['user']
    });

    return Promise.all(notes.map(async (note) => {
      return this.enrichNote(note, userId);
    }));
  }
}
