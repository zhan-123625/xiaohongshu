import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { NotesService } from './notes.service';
import { NotesController } from './notes.controller';
import { Note } from '../entities/note.entity';
import { Like } from '../entities/like.entity';
import { Collection } from '../entities/collection.entity';
import { Follow } from '../entities/follow.entity';
import { JwtModule } from '@nestjs/jwt';
import { jwtConstants } from '../auth/constants';

@Module({
  imports: [
    TypeOrmModule.forFeature([Note, Like, Collection, Follow]),
    JwtModule.register({
      secret: jwtConstants.secret,
      signOptions: { expiresIn: '60m' },
    }),
  ],
  controllers: [NotesController],
  providers: [NotesService],
})
export class NotesModule {}
