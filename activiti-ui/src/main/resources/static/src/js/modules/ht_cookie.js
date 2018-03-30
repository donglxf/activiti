/**
 * add by tanrq 2018/1/20
 */
layui.define([], function (exports) {
    var tokenCookieName = "token", refreshTokenCookieName = "refreshToken";
    var cookie = {
        /**
         * 新建cookie。
         * times为空字符串时,cookie的生存期至浏览器会话结束。hours为数字0时,建立的是一个失效的cookie,这个cookie会覆盖已经建立过的同名、同path的cookie（如果这个cookie存在）
         * @param name
         * @param value
         * @param times
         * @param path
         */
        setCookie: function (name, value, times, path) {
            var name = escape(name);
            var value = escape(value);
            var expires = new Date();
            expires.setMinutes(expires.getMinutes() + times);
            path = path == "" ? "" : ";path=" + path;
            var _expires = (typeof times) == "string" ? "" : ";expires=" + expires.toUTCString();
            document.cookie = name + "=" + value + _expires + path;
        },
        /**
         * 获取cookie
         * @param name
         * @returns {*}
         */
        getCookieValue: function (name) {
            var name = escape(name);
            //读cookie属性，这将返回文档的所有cookie
            var allcookies = document.cookie;
            //查找名为name的cookie的开始位置
            name += "=";
            var pos = allcookies.indexOf(name);
            //如果找到了具有该名字的cookie，那么提取并使用它的值
            //如果pos值为-1则说明搜索"version="失败
            if (pos != -1) {
                //cookie值开始的位置
                var start = pos + name.length;
                //从cookie值开始的位置起搜索第一个";"的位置,即cookie值结尾的位置
                var end = allcookies.indexOf(";", start);
                //如果end值为-1说明cookie列表里只有一个cookie
                if (end == -1) end = allcookies.length;
                //提取cookie的值
                var value = allcookies.substring(start, end);
                //对它解码
                return unescape(value);
            }
            else return "";
        }
        /**
         * 删除cookie
         * @param name
         * @param path
         */
        , deleteCookie: function (name, path) {
            var name = escape(name);
            var expires = new Date(0);
            path = path == "" ? "" : ";path=" + path;
            document.cookie = name + "=" + ";expires=" + expires.toUTCString() + path;
        }
        , setToken: function (token) {
            cookie.setCookie(tokenCookieName, token, 28, "/");
        }
        , setRefreshToken: function (refreshToken) {
            cookie.setCookie(refreshTokenCookieName, refreshToken, 30, "/");
        }
        , getToken: function () {
            return cookie.getCookieValue(tokenCookieName);
        }
        , getRefreshToken: function () {
            return cookie.getCookieValue(refreshTokenCookieName);
        }
        , deleteToken: function () {
            cookie.deleteCookie(tokenCookieName, "/");
        }
        , deleteRefreshToken: function () {
            cookie.deleteCookie(refreshTokenCookieName, "/");
        }
    };

    exports('ht_cookie', cookie);
});