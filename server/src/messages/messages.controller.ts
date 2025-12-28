import { Controller, Get, Post, Body, Param, UseGuards, Request } from '@nestjs/common';
import { MessagesService } from './messages.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('messages')
@UseGuards(JwtAuthGuard)
export class MessagesController {
  constructor(private readonly messagesService: MessagesService) {}

  @Post()
  async sendMessage(@Request() req, @Body() body: { receiverId: number; content: string }) {
    return this.messagesService.sendMessage(req.user.userId, body.receiverId, body.content);
  }

  @Get('conversations')
  async getConversations(@Request() req) {
    return this.messagesService.getConversations(req.user.userId);
  }

  @Get(':userId')
  async getMessages(@Request() req, @Param('userId') userId: number) {
    return this.messagesService.getMessages(req.user.userId, userId);
  }
}
