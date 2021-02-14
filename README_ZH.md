[English](README.md) | [简体中文](README_ZH.md)
# DDNS-for-Cloudflare
基于 Cloudflare APIv4 的 DDNS (Dynamic Domain Name Server，动态域名服务) 实践案例。

## 依赖服务
- [ip.renfei.net](https://ip.renfei.net)
- [cloudflare](https://gitee.com/rnf/cloudflare)

## 使用入门
首先，下载最新版的 Jar 包：[https://gitee.com/rnf/ddns-for-cloudflare/attach_files/613529/download/ddns-1.0.0.jar](https://gitee.com/rnf/ddns-for-cloudflare/attach_files/613529/download/ddns-1.0.0.jar)

运行 Jar 包并传递参数：

- -z zone：在 Cloudflare 管理的域名，例如：renfei.net
- -t token：在 Cloudflare 的 API 令牌，[https://dash.cloudflare.com/profile/api-tokens](https://dash.cloudflare.com/profile/api-tokens)
- -d domain：设置DDNS的域名，例如：test.renfei.net

案例：
```bash
java -jar ddns-1.0.0.jar -z renfei.net -t kK8tQ1gY1mV3hH4jJ9yN9zP8bL1hB6uU6vB2tT1o -d test.renfei.net
```
后台持续运行案例：
```bash
nohup java -jar ddns-1.0.0.jar -z renfei.net -t kK8tQ1gY1mV3hH4jJ9yN9zP8bL1hB6uU6vB2tT1o -d test.renfei.net >ddns.log 2>&1 &
```

## 开发调试
在运行调试前，设置```Program arguments```:
```bash
-z <zone> -t <token> -d <domain>
# 例如：
-z renfei.net -t kK8tQ1gY1mV3hH4jJ9yN9zP8bL1hB6uU6vB2tT1o -d test.renfei.net
```
![调试设置](document/image/debug_setting.png)