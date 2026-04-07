import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import * as RSSHub from 'rsshub';

// 计算.env文件的绝对路径
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const envPath = join(__dirname, '..', '..', '.env');

// 加载.env文件
dotenv.config({ path: envPath });

// 调试信息
console.log('Env path:', envPath);
console.log('PUPPETEER_EXECUTABLE_PATH:', process.env.PUPPETEER_EXECUTABLE_PATH);

// 从命令行读取参数
const keyword = process.argv[2];

if (!keyword) {
    console.log('请传入参数，包含关键词');
    process.exit(1);
}

await RSSHub.init();

// 拼接路由
const route = `/weibo/keyword/${encodeURIComponent(keyword)}`;

RSSHub.request(route)
    .then((data) => {
        console.log('获取成功');
        console.log(data);
    })
    .catch((e) => {
        console.log('报错：', e);
    });