const express = require('express');
const http = require('http');
const app = express();
const PORT = 3000;

// 中间件
app.use(express.json());
app.use(express.static(__dirname));

// 跨域支持
app.use((req, res, next) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  if (req.method === 'OPTIONS') return res.status(200).end();
  next();
});

// API 代理
app.post('/api/*', (req, res) => {
  const javaUrl = `http://localhost:8081${req.path.replace('/api', '')}`;
  let query = '';

  // 处理参数
  if (req.body.userId) {
    query = `?userId=${encodeURIComponent(req.body.userId)}`;
  } else if (req.body.keyword) {
    query = `?keyword=${encodeURIComponent(req.body.keyword)}`;
  }

  const fullUrl = javaUrl + query;
  console.log('转发到Java:', fullUrl);

  // 发送请求
  const request = http.get(fullUrl, (javaRes) => {
    let data = '';
    javaRes.on('data', chunk => data += chunk);
    javaRes.on('end', () => {
      res.setHeader('Content-Type', 'text/plain; charset=utf-8');
      res.send(data);
    });
  });

  request.on('error', (err) => {
    console.error('代理错误:', err.message);
    res.status(500).send(`连接Java服务器失败: ${err.message}`);
  });

  request.setTimeout(120000, () => {
    request.destroy();
    res.status(408).send('请求超时');
  });
});

// 启动
app.listen(PORT, () => {
  console.log(`前端: http://localhost:${PORT}`);
  console.log(`后端: http://localhost:8081`);
});