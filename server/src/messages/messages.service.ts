import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Message } from '../entities/message.entity';

@Injectable()
export class MessagesService {
  constructor(
    @InjectRepository(Message)
    private messagesRepository: Repository<Message>,
  ) {}

  async sendMessage(senderId: number, receiverId: number, content: string): Promise<Message> {
    const message = this.messagesRepository.create({
      senderId,
      receiverId,
      content,
    });
    return this.messagesRepository.save(message);
  }

  async getMessages(userId1: number, userId2: number): Promise<Message[]> {
    return this.messagesRepository.find({
      where: [
        { senderId: userId1, receiverId: userId2 },
        { senderId: userId2, receiverId: userId1 },
      ],
      order: { createdAt: 'ASC' },
      relations: ['sender', 'receiver'],
    });
  }

  async getConversations(userId: number): Promise<any[]> {
    // This is a bit complex in TypeORM to get unique latest conversations.
    // A simple approach is to get all messages involving the user, group by the other party, and pick the latest.
    // For simplicity/performance, we might use a raw query or just fetch recent messages and process in memory if not too many.
    
    // Using a raw query for better performance on conversation list
    const rawMessages = await this.messagesRepository
      .createQueryBuilder('message')
      .leftJoinAndSelect('message.sender', 'sender')
      .leftJoinAndSelect('message.receiver', 'receiver')
      .where('message.sender_id = :userId OR message.receiver_id = :userId', { userId })
      .orderBy('message.created_at', 'DESC')
      .getMany();

    const conversations = new Map<number, Message>();
    
    for (const msg of rawMessages) {
      const otherId = msg.senderId === userId ? msg.receiverId : msg.senderId;
      // Since we ordered by DESC, the first time we see a user, it's the latest message
      if (!conversations.has(otherId)) {
        conversations.set(otherId, msg);
      }
    }

    return Array.from(conversations.values());
  }
}
