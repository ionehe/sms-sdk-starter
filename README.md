# sms-sdk-starter

> 基于三方短信发送服务支持，通过配置不同的sms配置启用不同的短信支持

## 1 配置仓库

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>maven-releases</id>
            <mirrorOf>*</mirrorOf>
            <url>http://repo.ionehe.com/repository/maven-public/</url>
        </mirror>
        <mirror>
            <id>maven-snapshots</id>
            <mirrorOf>*</mirrorOf>
            <url>http://repo.ionehe.com/repository/maven-public/</url>
        </mirror>
    </mirrors>


    <profiles>
        <profile>
            <id>nexusProfile</id>
            <repositories>
                <repository>
                    <id>nexus</id>
                    <name>Nexus Public Repository</name>
                    <url>http://repo.ionehe.com/repository/maven-public/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>nexusProfile</activeProfile>
    </activeProfiles>
</settings>
```

## 2 maven配置

```text
    <dependency>
        <groupId>com.ionehe.public</groupId>
        <artifactId>sms-sdk-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
```

## 3 配置文件配置示例

```yaml
sms:
  vendor: bjlingxianhulian # 客户端配置，以下为目前已支持的所有客户端配置示例，使用者根据实际场景配置一种即可
  vendors:
    # 验证码类型templates固定为VerificationCode；通知类型自定义；paramsOrders为短信模板中的参数顺序
    # 发送验证码，不要修改VerificationCode
    aliyun: { endpoint: cn-hangzhou, accessKeyId: abcd, accessKeySecret: abcd,
              signName: abcd, templates: {VerificationCode: code}}
    bjlingxianhulian: { endpoint: 101.200.29.88:8082/SendMT/SendMessage, accessKeyId: abcd, accessKeySecret: abcd,
                        signName: abcd, templates: {VerificationCode: '验证码${code}，您正在尝试修改登录密码，请妥善保管账户信息。'}}
    qcloud: { appId: abcd, appKey: abcd,
              signName: abcd, templates: {VerificationCode: abcd}, paramsOrders: {VerificationCode: [code]}} 
  verificationCode:
    codeLength: 4 # 验证码长度
    expirationTime: 300 # 验证码过期时间单位秒
    retryIntervalTime: 60 # 重新发送验证码间隔时间,小于等于0表示不启用,单位秒
    templateHasExpirationTime: true # 模版中是否有过期时间字段
    deleteByVerifySucceed: true # 认证成功后删除验证码
```

## 4 示例代码，附sms-sdk-starter源码

## 4.1 代码仓库

[gitee](http://gitee.ionehe.com/demo/sms-sdk-starter)
[github](http://github.ionehe.com/demo/sms-sdk-starter)

## 4.2 接口验证

### 发送验证码
http://localhost:8080/send?phone=18435227980

### 校验验证码
http://localhost:8080/verify?phone=18435227980&code=4572

## 5 TODO

预留通知类短信接口，待完成接入

## changelog

- 接入阿里云短信服务
- 接入北京领先互联科技短信服务
- common-sms重构，调整为自动装配
- 接入腾讯云短信服务