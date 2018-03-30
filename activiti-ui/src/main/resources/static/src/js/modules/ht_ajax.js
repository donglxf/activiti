/**
 * add by tanrq 2018/1/20
 */
layui.define(["ht_cookie", "ht_config"], function (exports) {
        var $ = layui.jquery, cookie = layui.ht_cookie, ht_config = layui.ht_config;

        var ht_ajax = {
                config: {},
                set: function (options) {
                    var that = this;
                    $.extend(true, that.config, options);
                    return that;
                },
                init: function () {
                    var that = this;
                    ht_ajax.extendsAjax();
                    return that;
                },
                /**
                 * 验证token是否有效，如果无效则重新刷新token，如果无法拉取则重新登录
                 * @returns {boolean}
                 */
                validationAndRefreshToken: function (noToken) {
                    var token = cookie.getToken();
                    var refreshToken = cookie.getRefreshToken();
                    // 如果refreshToken失效，则直接跳转到登录页面
                    if (refreshToken == null || refreshToken == "") {
                        layer.confirm('登录超时，请重新登录!。', function (index) {
                            top.location.href = "/login.html";
                            layer.close(index);
                        });
                        return false;
                    } else {
                        //延长refreshToken时间
                        cookie.setRefreshToken(refreshToken);
                    }
                    // 验证token是否失效，失效则重新请求后台更新token，如果无法更新，则重新登录
                    if ((token == null || token == "") && (noToken == false || !noToken)) {
                        //console.debug("token无效，重新加载token");
                        $.ajax({
                            type: 'GET',
                            async: false,
                            url: ht_config.refreshTokenUrl,
                            headers: {
                                Authorization: "Bearer " + refreshToken
                            },
                            noToken: true,
                            success: function (data) {
                                if (data == null || data["token"] == null || data["token"] == "") {
                                    layer.confirm('登录超时，请重新登录。.', function (index) {
                                        top.location.href = "/login.html";
                                        layer.close(index);
                                    });
                                    return false;
                                } else {
                                    cookie.setToken(data["token"]);
                                    return true;
                                }
                            },
                            error: function () {
                                layer.confirm('登录超时，请重新登录。..', function (index) {
                                    top.location.href = "/login.html";
                                    layer.close(index);
                                });
                                return false;
                            }
                        });
                    }
                    return true;
                },
                /**
                 * 扩展jquery ajax方法
                 */
                extendsAjax: function () {
                    // 备份jquery的ajax方法
                    var _ajax = $.ajax;
                    var href = window.location.href;
                    if(href.indexOf(":8005/activiti/ui/")>0){
                        return _ajax;
                    }
                    // 重写jquery的ajax方法
                    $.ajax = function (opt) {
                        //所有ajax请求前先验证token是否存在
                        var isValidation = ht_ajax.validationAndRefreshToken(opt.noToken);
                        if (!isValidation) {
                            return false;
                        }
                        //因为可能存在同域名下的不同异步请求，所有不能通过地址过滤的方式来判断是否需要拼接basePath
                        // var ignoreUrl = ['.js', '.css', '.html', '.htm', '.png', '.gif', '.jpg', '.icon'];
                        // //自动拼接请求地址
                        // layui.each(ignoreUrl, function (index, item) {
                        //     if (opt.url && opt.url.toUpperCase().indexOf(item.toUpperCase()) > 0) {
                        //         console.info(opt.url, item);
                        //     }
                        // });

                        // 备份opt中error和success方法
                        var fn = {
                            error: function (XMLHttpRequest, textStatus, errorThrown) {
                            },
                            success: function (data, textStatus) {
                            }
                        }
                        if (opt.error) {
                            fn.error = opt.error;
                        }
                        if (opt.success) {
                            fn.success = opt.success;
                        }

                        // 扩展增强处理
                        $.extend(opt, {
                                error: function (XMLHttpRequest, textStatus, errorThrown) {
                                    //TODO 需判断是否token失效，和API权限是否验证通过
                                    fn.error(XMLHttpRequest, textStatus, errorThrown);
                                },
                                success: function (data, textStatus) {
                                    if (data != null) {
                                        switch (data["status_code"]) {
                                            case "9921"://TOKEN过期
                                            case "9922"://验签失败
                                            case "9924"://授权失败
                                            case "9925"://TOKEN不能为空
                                                layer.confirm(data['result_msg'] + '，请重新登录。', function (index) {
                                                    top.location.href = "login.html";
                                                    layer.close(index);
                                                });
                                                return false;
                                            case "9926":
                                                layer.alert("对不起，您没有该功能的操作权限，如有疑问，请联系管理员。");
                                                return false;
                                        }
                                    }
                                    fn.success(data, textStatus);
                                },
                                complete: function (XHR, TS) {
                                    // 请求完成后回调函数 (请求成功或失败之后均调用)。
                                }
                            }
                        );

                        var headers = {
                            app: ht_config.app /*系统编码统一通过http headers进行传输*/
                        }
                        //noToken 默认为false，当为true时，则不传输token
                        if (opt.noToken == false || !opt.noToken) {
                            headers.Authorization = "Bearer " + cookie.getToken();
                        }
                        headers = $.extend({}, opt.headers, headers);
                        $.extend(opt, {
                            headers: $.extend({}, opt.headers, headers)
                        });

                        return _ajax(opt);
                    }
                    ;
                }
            }
        ;

        ht_ajax.init();
        exports('ht_ajax', ht_ajax);
    }
)
;