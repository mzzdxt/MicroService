# MicroService
基于Android系统的微服务器

> 在慕课网学习的《手机服务器微架构设计与实现》，课程地址：http://www.imooc.com/learn/676

### 总结一下遇到的坑吧

- 首先就是使用模拟器调试的时候，需要记住以下几个命令
  - telnet localhost 5554[你自己模拟器的端口，用于连接到模拟器]
  - auth xxxxxx[进行授权]
  - redir list[optional 查看模拟器和电脑的端口映射]
  - redir add tcp:[pc端口]:[模拟器端口]
- 在处理request的header时候，request中请求头的“Content-Length”均为小写，我在处理的时候按照前面写的格式进行了处理，结果死活得不到数据，后来把请求头中的所有字段都打印了出来才发现这个问题
- Socket中主要就是流的处理

