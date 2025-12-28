import { Controller, Get, Post, Body, Query, Param, UseGuards, Req, Logger } from '@nestjs/common';
import { CommentsService } from './comments.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('comments')
export class CommentsController {
  private readonly logger = new Logger(CommentsController.name);

  constructor(private readonly commentsService: CommentsService) {}

  @UseGuards(JwtAuthGuard)
  @Post('note/:noteId')
  async create(@Param('noteId') noteId: number, @Body() body: { content: string }, @Req() req) {
    this.logger.log(`create: noteId=${noteId}, userId=${req.user.userId}, content=${body.content}`);
    return this.commentsService.create({
      noteId,
      content: body.content,
      userId: req.user.userId,
    });
  }

  @Get('note/:noteId')
  async findByNoteId(@Param('noteId') noteId: number) {
    this.logger.log(`findByNoteId: noteId=${noteId}`);
    return this.commentsService.findByNoteId(noteId);
  }
}
