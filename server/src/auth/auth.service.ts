import { Injectable, UnauthorizedException, Logger } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { JwtService } from '@nestjs/jwt';

/**
 * 认证服务
 * 处理登录验证、Token生成和用户注册逻辑
 */
@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(
    private usersService: UsersService,
    private jwtService: JwtService
  ) {}

  /**
   * 用户登录
   * @param phone 手机号
   * @param pass 密码
   * @returns 包含 access_token 的对象
   */
  async signIn(phone: string, pass: string): Promise<any> {
    this.logger.log(`开始处理登录: Phone=${phone}`);
    let user = await this.usersService.findByPhone(phone);

    // 如果用户不存在，为了方便测试，进行自动注册
    if (!user) {
        this.logger.warn(`用户不存在，尝试自动注册: Phone=${phone}`);
        try {
            user = await this.usersService.create({
                phone: phone,
                passwordHash: pass,
                username: `User_${phone.slice(-4)}`,
                role: 0, // 默认普通用户
                status: 1 // 默认正常状态
            });
            this.logger.log(`自动注册成功: ID=${user.id}`);
        } catch (e) {
            this.logger.error('自动注册失败', e);
            throw new UnauthorizedException('无法创建新用户');
        }
    }

    // 验证密码 (注意：实际生产环境应使用 bcrypt 进行哈希比对)
    if (user && user.passwordHash === pass) {
      const payload = { sub: user.id, username: user.username, role: user.role };
      this.logger.log(`密码验证通过，生成 Token: UserID=${user.id}`);
      return {
        access_token: await this.jwtService.signAsync(payload),
      };
    }
    
    this.logger.warn(`登录失败: Phone=${phone}, 密码不匹配`);
    throw new UnauthorizedException();
  }
  
  /**
   * 用户注册
   * @param user 用户信息
   * @returns 创建的用户实体
   */
  async register(user: any) {
      this.logger.log(`处理注册请求: Phone=${user.phone}`);
      // 简单的密码字段映射处理
      if (user.password && !user.passwordHash) {
          user.passwordHash = user.password;
          delete user.password;
      }
      return this.usersService.create(user);
  }
}
