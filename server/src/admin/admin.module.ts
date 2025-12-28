import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AdminController } from './admin.controller';
import { AdminService } from './admin.service';
import { User } from '../entities/user.entity';
import { Note } from '../entities/note.entity';
import { Comment } from '../entities/comment.entity';
import { AdminLog } from '../entities/admin-log.entity';

@Module({
  imports: [TypeOrmModule.forFeature([User, Note, Comment, AdminLog])],
  controllers: [AdminController],
  providers: [AdminService],
})
export class AdminModule {}
