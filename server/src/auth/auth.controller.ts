import { Body, Controller, Post, HttpCode, HttpStatus, Logger } from '@nestjs/common';
import { AuthService } from './auth.service';

/**
 * 认证控制器
 * 处理用户登录和注册请求
 */
@Controller('auth')
export class AuthController {
  private readonly logger = new Logger(AuthController.name);

  constructor(private authService: AuthService) {}

  /**
   * 用户登录接口
   * @param signInDto 包含 phone 和 password 的对象
   * @returns JWT token
   */
  @HttpCode(HttpStatus.OK)
  @Post('login')
  signIn(@Body() signInDto: Record<string, any>) {
    this.logger.log(`收到登录请求: Phone=${signInDto.phone}`);
    return this.authService.signIn(signInDto.phone, signInDto.password);
  }

  /**
   * 用户注册接口
   * @param registerDto 用户注册信息
   * @returns 注册成功的用户信息
   */
  @Post('register')
  register(@Body() registerDto: Record<string, any>) {
    this.logger.log(`收到注册请求: Phone=${registerDto.phone}`);
    return this.authService.register(registerDto);
  }
}
