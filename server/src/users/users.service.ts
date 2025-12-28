import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, In } from 'typeorm';
import { User } from '../entities/user.entity';
import { Follow } from '../entities/follow.entity';
import { Like } from '../entities/like.entity';
import { Note } from '../entities/note.entity';
import { Collection } from '../entities/collection.entity';
import { Block } from '../entities/block.entity';

/**
 * 用户服务
 * 处理用户数据的 CRUD 操作
 */
@Injectable()
export class UsersService {
  private readonly logger = new Logger(UsersService.name);

  constructor(
    @InjectRepository(User)
    private usersRepository: Repository<User>,
    @InjectRepository(Follow)
    private followsRepository: Repository<Follow>,
    @InjectRepository(Like)
    private likesRepository: Repository<Like>,
    @InjectRepository(Note)
    private notesRepository: Repository<Note>,
    @InjectRepository(Collection)
    private collectionsRepository: Repository<Collection>,
    @InjectRepository(Block)
    private blocksRepository: Repository<Block>,
  ) {}

  /**
   * 根据用户名查找用户
   * @param username 用户名
   */
  async findOne(username: string): Promise<User | undefined> {
    this.logger.log(`findOne: username=${username}`);
    return this.usersRepository.findOne({ where: { username } });
  }

  /**
   * 根据手机号查找用户
   * @param phone 手机号
   */
  async findByPhone(phone: string): Promise<User | undefined> {
    this.logger.log(`findByPhone: phone=${phone}`);
    return this.usersRepository.findOne({ where: { phone } });
  }

  /**
   * 根据 ID 查找用户
   * @param id 用户ID
   */
  async findOneById(id: number): Promise<User | undefined> {
    this.logger.log(`findOneById: id=${id}`);
    const user = await this.usersRepository.findOne({ where: { id } });
    if (!user) return undefined;

    // 实时计算统计数据
    const followingCount = await this.followsRepository.count({ where: { followerId: id } });
    const followerCount = await this.followsRepository.count({ where: { followingId: id } });
    
    // 计算获赞数 (用户发布的所有笔记收到的点赞总数)
    const notes = await this.notesRepository.find({ where: { userId: id }, select: ['id'] });
    let likeCount = 0;
    if (notes.length > 0) {
        const noteIds = notes.map(n => n.id);
        likeCount = await this.likesRepository.count({ where: { targetId: In(noteIds), type: 1 } });
    }

    user.followingCount = followingCount;
    user.followerCount = followerCount;
    user.likeCount = likeCount;

    // 更新数据库中的统计数据
    await this.usersRepository.save(user);

    return user;
  }

  /**
   * 创建新用户
   * @param userData 用户数据
   */
  async create(userData: Partial<User>): Promise<User> {
    this.logger.log(`创建新用户: Phone=${userData.phone}`);
    const user = this.usersRepository.create(userData);
    return this.usersRepository.save(user);
  }

  /**
   * 关注/取消关注用户
   * @param followerId 关注者ID
   * @param followingId 被关注者ID
   */
  async toggleFollow(followerId: number, followingId: number): Promise<{ isFollowing: boolean }> {
    this.logger.log(`toggleFollow: followerId=${followerId}, followingId=${followingId}`);
    if (followerId === followingId) {
        throw new Error("Cannot follow yourself");
    }

    const existing = await this.followsRepository.findOne({ where: { followerId, followingId } });
    if (existing) {
      await this.followsRepository.remove(existing);
      this.logger.log(`toggleFollow: Unfollowed`);
      return { isFollowing: false };
    } else {
      const follow = this.followsRepository.create({ followerId, followingId });
      await this.followsRepository.save(follow);
      this.logger.log(`toggleFollow: Followed`);
      return { isFollowing: true };
    }
  }

  /**
   * 检查是否关注
   */
  async isFollowing(followerId: number, followingId: number): Promise<boolean> {
      const count = await this.followsRepository.count({ where: { followerId, followingId } });
      return count > 0;
  }

  /**
   * 获取关注列表
   */
  async getFollowing(userId: number): Promise<User[]> {
    const follows = await this.followsRepository.find({
      where: { followerId: userId },
      relations: ['following'],
      order: { createdAt: 'DESC' }
    });
    return follows.map(f => f.following);
  }

  /**
   * 获取粉丝列表
   */
  async getFollowers(userId: number): Promise<User[]> {
    const follows = await this.followsRepository.find({
      where: { followingId: userId },
      relations: ['follower'],
      order: { createdAt: 'DESC' }
    });
    return follows.map(f => f.follower);
  }

  /**
   * 获取收到的赞
   */
  async getReceivedLikes(userId: number): Promise<any[]> {
    const notes = await this.notesRepository.find({ where: { userId }, select: ['id'] });
    const noteIds = notes.map(n => n.id);
    if (noteIds.length === 0) return [];

    const likes = await this.likesRepository.find({
      where: { targetId: In(noteIds), type: 1 },
      relations: ['user'],
      order: { createdAt: 'DESC' }
    });
    
    return likes.map(like => ({
        ...like,
        noteId: like.targetId
    }));
  }

  /**
   * 获取收到的收藏
   */
  async getReceivedCollections(userId: number): Promise<any[]> {
    const notes = await this.notesRepository.find({ where: { userId }, select: ['id'] });
    const noteIds = notes.map(n => n.id);
    if (noteIds.length === 0) return [];

    return this.collectionsRepository.find({
      where: { noteId: In(noteIds) },
      relations: ['user'],
      order: { createdAt: 'DESC' }
    });
  }

  /**
   * 获取未读通知数量
   */
  async getUnreadCounts(userId: number): Promise<{ likes: number, collections: number, followers: number }> {
    // 1. Unread Followers
    const followers = await this.followsRepository.count({ where: { followingId: userId, isRead: false } });

    // 2. Get User's Note IDs
    const notes = await this.notesRepository.find({ where: { userId }, select: ['id'] });
    const noteIds = notes.map(n => n.id);

    let likes = 0;
    let collections = 0;

    if (noteIds.length > 0) {
        // 3. Unread Likes
        likes = await this.likesRepository.count({ where: { targetId: In(noteIds), type: 1, isRead: false } });
        // 4. Unread Collections
        collections = await this.collectionsRepository.count({ where: { noteId: In(noteIds), isRead: false } });
    }

    return { likes, collections, followers };
  }

  /**
   * 标记通知为已读
   */
  async markAsRead(userId: number, type: string): Promise<void> {
      // type: 'likes', 'collections', 'followers'
      if (type === 'followers') {
          await this.followsRepository.update({ followingId: userId, isRead: false }, { isRead: true });
      } else {
          const notes = await this.notesRepository.find({ where: { userId }, select: ['id'] });
          const noteIds = notes.map(n => n.id);
          if (noteIds.length > 0) {
              if (type === 'likes') {
                  await this.likesRepository.update({ targetId: In(noteIds), type: 1, isRead: false }, { isRead: true });
              } else if (type === 'collections') {
                  await this.collectionsRepository.update({ noteId: In(noteIds), isRead: false }, { isRead: true });
              }
          }
      }
  }

  /**
   * 拉黑用户
   */
  async blockUser(blockerId: number, blockedId: number): Promise<void> {
    const existingBlock = await this.blocksRepository.findOne({
      where: { blockerId, blockedId },
    });

    if (existingBlock) {
      return;
    }

    const block = this.blocksRepository.create({
      blockerId,
      blockedId,
    });
    await this.blocksRepository.save(block);
    
    // Also unfollow if following
    await this.followsRepository.delete({ followerId: blockerId, followingId: blockedId });
    // And remove from followers if they follow us (optional, but common behavior)
    await this.followsRepository.delete({ followerId: blockedId, followingId: blockerId });
  }

  /**
   * 取消拉黑
   */
  async unblockUser(blockerId: number, blockedId: number): Promise<void> {
    await this.blocksRepository.delete({ blockerId, blockedId });
  }
}
