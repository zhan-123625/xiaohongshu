import { Module, NestModule, MiddlewareConsumer } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersModule } from './users/users.module';
import { NotesModule } from './notes/notes.module';
import { AuthModule } from './auth/auth.module';
import { User } from './entities/user.entity';
import { Note } from './entities/note.entity';
import { CommentsModule } from './comments/comments.module';
import { Comment } from './entities/comment.entity';
import { Like } from './entities/like.entity';
import { Collection } from './entities/collection.entity';
import { Follow } from './entities/follow.entity';
import { AdminLog } from './entities/admin-log.entity';
import { LoggerMiddleware } from './common/middleware/logger.middleware';
import { AdminModule } from './admin/admin.module';
import { MessagesModule } from './messages/messages.module';
import { Message } from './entities/message.entity';
import { Block } from './entities/block.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: 'localhost',
      port: 3306,
      username: 'root',
      password: '',
      database: 'xiaohongshu',
      entities: [User, Note, Comment, Like, Collection, Follow, AdminLog, Message, Block],
      synchronize: true, // Don't use this in production
    }),
    UsersModule,
    NotesModule,
    AuthModule,
    CommentsModule,
    AdminModule,
    MessagesModule,
  ],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggerMiddleware).forRoutes('*');
  }
}
