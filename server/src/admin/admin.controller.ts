import { Controller, Get, Patch, Delete, Param, Body, Query, UseGuards, Request, Ip } from '@nestjs/common';
import { AdminService } from './admin.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';

@Controller('admin')
@UseGuards(JwtAuthGuard, RolesGuard)
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Get('stats/overview')
  async getStats() {
    return this.adminService.getStats();
  }

  @Get('users')
  async getUsers(@Query('page') page: number, @Query('limit') limit: number) {
    return this.adminService.getUsers(page, limit);
  }

  @Patch('users/:id/status')
  async updateUserStatus(
    @Request() req,
    @Param('id') id: number,
    @Body('status') status: number,
    @Ip() ip: string
  ) {
    return this.adminService.updateUserStatus(req.user.userId, id, status, ip);
  }

  @Get('notes')
  async getNotes(
    @Query('page') page: number,
    @Query('limit') limit: number,
    @Query('status') status?: number
  ) {
    return this.adminService.getNotes(page, limit, status);
  }

  @Patch('notes/:id/audit')
  async auditNote(
    @Request() req,
    @Param('id') id: number,
    @Body('status') status: number,
    @Body('reason') reason: string,
    @Ip() ip: string
  ) {
    return this.adminService.auditNote(req.user.userId, id, status, reason, ip);
  }

  @Delete('comments/:id')
  async deleteComment(@Request() req, @Param('id') id: number, @Ip() ip: string) {
    return this.adminService.deleteComment(req.user.userId, id, ip);
  }
}
