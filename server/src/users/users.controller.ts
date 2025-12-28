import { Controller, Get, Post, Body, UseGuards, Request, NotFoundException, Logger, Param, Req } from '@nestjs/common';
import { UsersService } from './users.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('users')
export class UsersController {
  private readonly logger = new Logger(UsersController.name);

  constructor(private readonly usersService: UsersService) {}

  @Post('register')
  async register(@Body() body: any) {
    this.logger.log(`register: body=${JSON.stringify(body)}`);
    // Implementation for registration
    return this.usersService.create(body);
  }

  @UseGuards(JwtAuthGuard)
  @Get('profile')
  async getProfile(@Request() req) {
    this.logger.log(`getProfile: userId=${req.user.userId}`);
    // req.user contains { userId, username } from JwtStrategy
    const user = await this.usersService.findOneById(req.user.userId);
    if (!user) {
      this.logger.warn(`getProfile: User not found userId=${req.user.userId}`);
      throw new NotFoundException('User not found');
    }
    return user;
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/follow')
  async toggleFollow(@Param('id') id: number, @Request() req) {
    this.logger.log(`toggleFollow: userId=${req.user.userId}, targetId=${id}`);
    return this.usersService.toggleFollow(req.user.userId, id);
  }

  @Get(':id/following')
  async getFollowing(@Param('id') id: number) {
    this.logger.log(`getFollowing: userId=${id}`);
    return this.usersService.getFollowing(id);
  }

  @Get(':id/followers')
  async getFollowers(@Param('id') id: number) {
    this.logger.log(`getFollowers: userId=${id}`);
    return this.usersService.getFollowers(id);
  }

  @Get(':id/likes-received')
  async getReceivedLikes(@Param('id') id: number) {
    this.logger.log(`getReceivedLikes: userId=${id}`);
    return this.usersService.getReceivedLikes(id);
  }

  @Get(':id/collections-received')
  async getReceivedCollections(@Param('id') id: number) {
    this.logger.log(`getReceivedCollections: userId=${id}`);
    return this.usersService.getReceivedCollections(id);
  }

  @UseGuards(JwtAuthGuard)
  @Get('notifications/unread-counts')
  async getUnreadCounts(@Req() req) {
    this.logger.log(`getUnreadCounts: userId=${req.user.userId}`);
    return this.usersService.getUnreadCounts(req.user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Post('notifications/mark-read')
  async markAsRead(@Req() req, @Body() body: { type: string }) {
    this.logger.log(`markAsRead: userId=${req.user.userId}, type=${body.type}`);
    return this.usersService.markAsRead(req.user.userId, body.type);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/block')
  async blockUser(@Param('id') id: number, @Request() req) {
    this.logger.log(`blockUser: userId=${req.user.userId}, targetId=${id}`);
    await this.usersService.blockUser(req.user.userId, id);
    return { success: true };
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/unblock')
  async unblockUser(@Param('id') id: number, @Request() req) {
    this.logger.log(`unblockUser: userId=${req.user.userId}, targetId=${id}`);
    await this.usersService.unblockUser(req.user.userId, id);
    return { success: true };
  }
}
