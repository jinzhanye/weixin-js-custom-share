# 使用weinxin.js自定义微信分享卡片信息

下面以 http://www.example.com 为例，讲解如何自定义微信分享卡片信息。

前提：公众号必须已认证

## 配置
### 公众号平台配置
- 获取 MP\_verify_[任意字符].txt、AppId、AppSecret
- 配置可信域名 http://www.example.com
- 配置安全域名 http://www.example.com
- 配置服务器ip到ip白名单

### 代码配置

- 将 MP\_verify_[任意字符].txt 放在前端项目的根目录
- 将 AppId、AppSecret 配置到 WeixinController
- 在share.js配置分享信息
    
    其中这段代码要特别注意，因为weixin.js会给当前url添加一些信息用作后台签名认证，所以url须动态获取!! 而 `location.href.split('#')[0]` 获取的正是当前url，`location.href` 后面的内容是weixin.js添加的，我们只需 `#` 前面的部分 
    
    ````js
    function getSignUrl() {
        var signUrl = location.href.split('#')[0];
        signUrl = encodeURIComponent(signUrl);
        return baseUrl + '/weixin/sign?url=' + signUrl;
    }
    ````

## 调试

### 清理微信浏览器缓存

项目重新部署后一定要清理浏览器缓存，否则很有可能看不到效果。安卓打开 [debugx5.qq.com ](debugx5.qq.com) 清理，苹果可以直接刷新页面清理。

详见[微信浏览为网站的缓存怎么清理?](https://www.zhihu.com/question/22471239)

### 确保 token、jsapi_ticket 获取成功

确保token获取成功，可以在 WeixinController line44 打断点查看结果，返回success即成功，否则返回失败码，可以在微信公众平台技术文档查阅[全局返回码说明](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1433747234)。同理确保jsapi_ticket获取成功，可以在 WeixinController line72 打断点查看结果

这两个后台调试都可以在本地进行，只不过因为本地调试使用localhost不在公众号的配置可信域名，所以在微信开发者工具输入前端页面会报 invalidate signature 错误。但是对于验证能否成功获取token、jsapi_ticket，本地调试比线上调试方便许多。

### invalidate signature 错误

原因有以下几种可能 

- wx.config 的 link 字段不在公众号配置的可信域名
- 验证签名时没有动态获取url，也就是上面代码配置提到的 `getSignUrl` 函数
