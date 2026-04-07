require('dotenv').config();
const RSSHub = require('rsshub');

RSSHub.init({
    // config
});

RSSHub.request('/bilibili/bangumi/media/23679586')
    .then((data) => {
        console.log(data);
    })
    .catch((e) => {
        console.log(e);
    });

RSSHub.request('/bilibili/user/dynamic/381780139')
    .then((data) => {
        console.log(data);
    })
    .catch((e) => {
        console.log(e);
    });