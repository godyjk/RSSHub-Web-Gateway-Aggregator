require('dotenv').config();
const RSSHub = require('rsshub');

RSSHub.init();

RSSHub.request('/zhihu/hot')
    .then((data) => {
        console.log('获取成功');
        console.log(data);
    })
    .catch((e) => {
        console.log('报错：', e);
    });
