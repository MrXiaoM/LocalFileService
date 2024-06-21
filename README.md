# LocalFileService

Overflow 的附属模块，目前用于更改图片、语音、短视频消息的文件上传方案，使用更优雅的方法兼容 mirai 原本的上传文件。

适用于 **mirai** 与 **Onebot 协议实现** 部署在同一机器下，可访问同一文件系统的情形。

当负载在 mirai 上传图片、语音、短视频时，插件提供的文件服务将会把数据流保存到本地临时文件，并将文件路径回传，用于发送到 Onebot 协议端。  
以此覆盖掉 Overflow 自带的 Base64 文件服务，可以使得日志文件占用更小，更方便查看最近上传文件。

# mirai-console 使用

安装本插件，启动后到配置文件 `config/top.mrxiaom.overflow-local-file-service/config.yml` 设置以下选项
+ `保存路径`，默认为 `data/top.mrxiaom.overflow-local-file-service`
+ `文件保存时间(天)`，默认为 `7天`
+ `如果要上传的是本地文件，是否直接使用本地文件`，默认为 `开启`

等。使用命令 `/lfs reload` 重载配置即可。

# mirai-core 使用

请参考本插件源码 `LocalFileService.kt`
调用 `register()` 即可注册该服务
