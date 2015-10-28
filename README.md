# LeanCloud_SMS_Tutorial 使用指南

本项目实现的功能如下：

1. 演示用户的注册和登陆
2. 演示用户手机号验证
3. 演示发送通知类短信
4. 演示通过短信进行敏感操作验证

## 如何本地调试

在 Android Studio 里面打开 `app/src/main/java/cn/leancloud/smstutorial/MyApplication.java` ，将如下代码中的

```
AVOSCloud.initialize(this,"YOUR APP ID","YOUR APP KEY");
```
其中的参数换成自己的 AppID 以及 AppKey

然后在 Android Studio 中点击编译或者直接运行，等待 gradle build（第一次 build 会耗时较长）




