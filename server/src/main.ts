import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Logger } from '@nestjs/common';

async function bootstrap() {
  // 创建 NestJS 应用实例
  const app = await NestFactory.create(AppModule);
  
  // 启用 CORS 以允许跨域请求 (方便前端开发)
  app.enableCors();

  const port = 3000;
  await app.listen(port);
  
  Logger.log(`🚀 服务端已启动，监听端口: ${port}`, 'Bootstrap');
}
bootstrap();
