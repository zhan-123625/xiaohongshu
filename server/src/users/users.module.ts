import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersService } from './users.service';
import { UsersController } from './users.controller';
import { User } from '../entities/user.entity';
import { Follow } from '../entities/follow.entity';
import { Like } from '../entities/like.entity';
import { Note } from '../entities/note.entity';
import { Collection } from '../entities/collection.entity';
import { Block } from '../entities/block.entity';

@Module({
  imports: [TypeOrmModule.forFeature([User, Follow, Like, Note, Collection, Block])],
  providers: [UsersService],
  controllers: [UsersController],
  exports: [UsersService],
})
export class UsersModule {}
