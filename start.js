// D:\project\rsscatch\start.js
const { spawn } = require('child_process');
const { join } = require('path');
const { exec } = require('child_process');

console.log('🚀 启动数据抓取工具...\n');

// 获取项目根目录
const projectRoot = __dirname;
const javaDir = join(projectRoot, 'application-controller');
const uiDir = join(projectRoot, 'user-UI');

console.log(`项目根目录: ${projectRoot}`);

// 在外部作用域声明变量
let javaServer = null;
let nodeServer = null;

// 清理函数
function cleanup() {
  console.log('\n🛑 正在停止服务...');
  if (javaServer) {
    javaServer.kill();
  }
  if (nodeServer) {
    nodeServer.kill();
  }
  process.exit(0);
}

// 注册信号处理
process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);
process.on('exit', cleanup);

// 1. 启动 Java 后端服务器 (端口 8081)
console.log('1. 启动 Java 后端 (端口 8081)');
javaServer = spawn('java', ['interfaceServer'], {
  cwd: javaDir,
  shell: true
});

javaServer.stdout.on('data', data => {
  const output = data.toString().trim();
  if (output) console.log(`[Java] ${output}`);
});

javaServer.stderr.on('data', data => {
  const output = data.toString().trim();
  if (output) console.log(`[Java] ${output}`);
});

// 2. 启动 Node.js 前端服务器 (端口 3000)
setTimeout(() => {
  console.log('\n2. 启动 Node.js 前端 (端口 3000)');
  nodeServer = spawn('node', ['server.js'], {
    cwd: uiDir,
    shell: true
  });

  nodeServer.stdout.on('data', data => {
    const output = data.toString().trim();
    if (output) console.log(`[Node] ${output}`);
  });

  nodeServer.stderr.on('data', data => {
    console.log(`[Node] ${data.toString().trim()}`);
  });
}, 2000);

// 3. 自动打开浏览器
setTimeout(() => {
  console.log('\n3. 自动打开浏览器...');
  console.log('   🌐 访问: http://localhost:3000');
  console.log('   🎯 按 Ctrl+C 停止服务\n');

  // 自动打开浏览器
  const platform = process.platform;
  const url = 'http://localhost:3000';

  if (platform === 'win32') {
    exec(`start ${url}`);
  } else if (platform === 'darwin') {
    exec(`open ${url}`);
  } else if (platform === 'linux') {
    exec(`xdg-open ${url}`);
  } else {
    console.log(`请手动打开浏览器访问: ${url}`);
  }
}, 4000);