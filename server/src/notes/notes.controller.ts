import { Controller, Get, Post, Delete, Body, Query, Param, UseGuards, Req, Headers, Logger, UseInterceptors, UploadedFile } from '@nestjs/common';
import { NotesService } from './notes.service';
import { CreateNoteDto } from './dto/create-note.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { JwtService } from '@nestjs/jwt';
import { FileInterceptor } from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import { extname } from 'path';

@Controller('notes')
export class NotesController {
  private readonly logger = new Logger(NotesController.name);

  constructor(
    private readonly notesService: NotesService,
    private readonly jwtService: JwtService
  ) {}

  private getUserIdFromHeader(authHeader: string): number | undefined {
    if (!authHeader) return undefined;
    try {
      const token = authHeader.split(' ')[1];
      const decoded = this.jwtService.verify(token);
      return decoded.sub;
    } catch (e) {
      this.logger.warn(`Failed to decode token: ${e.message}`);
      return undefined;
    }
  }

  @Get('feed')
  async getFeed(@Query('page') page: number, @Query('limit') limit: number, @Headers('authorization') authHeader: string) {
    this.logger.log(`getFeed: page=${page}, limit=${limit}`);
    const userId = this.getUserIdFromHeader(authHeader);
    this.logger.log(`getFeed: userId=${userId}`);
    return this.notesService.findAll(page, limit, userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('my')
  async getMyNotes(@Req() req) {
    this.logger.log(`getMyNotes: userId=${req.user.userId}`);
    return this.notesService.findByUserId(req.user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('liked')
  async getLikedNotes(@Req() req) {
    this.logger.log(`getLikedNotes: userId=${req.user.userId}`);
    return this.notesService.findLikedByUserId(req.user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('collected')
  async getCollectedNotes(@Req() req) {
    this.logger.log(`getCollectedNotes: userId=${req.user.userId}`);
    return this.notesService.findCollectedByUserId(req.user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Post('upload')
  @UseInterceptors(FileInterceptor('file', {
    storage: diskStorage({
      destination: './uploads',
      filename: (req, file, callback) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        const ext = extname(file.originalname);
        callback(null, `${file.fieldname}-${uniqueSuffix}${ext}`);
      },
    }),
  }))
  async uploadFile(@UploadedFile() file: Express.Multer.File) {
    this.logger.log(`File uploaded: ${file.filename}`);
    // Return the URL of the uploaded file
    // Assuming the server is running on localhost:3000
    // In production, this should be configured via environment variables
    return { url: `http://10.0.2.2:3000/uploads/${file.filename}` };
  }

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(@Body() createNoteDto: CreateNoteDto, @Req() req) {
    this.logger.log(`create: userId=${req.user.userId}, dto=${JSON.stringify(createNoteDto)}`);
    return this.notesService.create({
      ...createNoteDto,
      userId: req.user.userId,
    });
  }

  @Get(':id')
  async findOne(@Param('id') id: number, @Headers('authorization') authHeader: string) {
    this.logger.log(`findOne: id=${id}`);
    const userId = this.getUserIdFromHeader(authHeader);
    return this.notesService.findOne(id, userId);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/like')
  async toggleLike(@Param('id') id: number, @Req() req) {
    this.logger.log(`toggleLike: userId=${req.user.userId}, noteId=${id}`);
    return this.notesService.toggleLike(req.user.userId, id);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/collect')
  async toggleCollect(@Param('id') id: number, @Req() req) {
    this.logger.log(`toggleCollect: userId=${req.user.userId}, noteId=${id}`);
    return this.notesService.toggleCollect(req.user.userId, id);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  async delete(@Param('id') id: number, @Req() req) {
    this.logger.log(`delete: userId=${req.user.userId}, noteId=${id}`);
    return this.notesService.delete(id, req.user.userId);
  }
}
