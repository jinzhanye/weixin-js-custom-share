var baseUrl = 'http://www.example.com/api'; // 后台api地址
var sharedUrl = 'http://www.example.com/index.html';

var config = {
    title: '分享标题',
    desc: '描述',
    imgUrl: ' 分享图标',
    link: sharedUrl, // 分享链接，该链接域名必须与当前企业的可信域名一致
    type: '',
    dataUrl: '',
    success: function () {
        // 用户确认分享后执行的回调函数
    },
    cancel: function () {
        // 用户取消分享后执行的回调函数
    }
};

function getSignUrl() {
    var signUrl = location.href.split('#')[0];
    signUrl = encodeURIComponent(signUrl);
    return baseUrl + '/weixin/sign?url=' + signUrl;
}

function doConfig(result) {
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: result.appId, // 必填，企业号的唯一标识，此处填写企业号corpid
        timestamp: result.timestamp, // 必填，生成签名的时间戳
        nonceStr: result.nonceStr, // 必填，生成签名的随机串
        signature: result.signature,// 必填，签名，见附录1
        jsApiList: [
            'onMenuShareAppMessage',
            'onMenuShareTimeline'
        ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
    });

    wx.ready(function () {
        // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
        // 分享到朋友圈
        wx.onMenuShareTimeline(config);
        //分享给朋友
        wx.onMenuShareAppMessage(config);
    });
}


$.get(getSignUrl(), function (result, status) {
    if (status === 'success') {
        doConfig(result);
    } else {
        alert('无法获取token信息');
    }
});

