[English](README.md) | [简体中文](README_ZH.md)
# DDNS-for-Cloudflare
The DDNS (Dynamic Domain Name Server) practice case based on Cloudflare APIv4.

## Dependent Service
- [ip.renfei.net](https://ip.renfei.net)
- [cloudflare](https://gitee.com/rnf/cloudflare)

## Development and Debugging
Before debugging, set the```Program arguments```:
```bash
-z <zone> -t <token> -d <domain>
# example:
-z renfei.net -t kK8tQ1gY1mV3hH4jJ9yN9zP8bL1hB6uU6vB2tT1o -d test.renfei.net
```
![Debug settings](document/image/debug_setting.png)