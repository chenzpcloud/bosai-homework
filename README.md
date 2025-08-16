# 运行
执行 java -jar bosai-homeworks-1.0.0.jar 即可运行 该项目


服务将在8001端口启动。
### 工程结构
```
Application  应用启动类，包含构建Http服务，创建会话，处理获取会话、投注，查询最高投注列表请求
handler包为具体请求分发后的处理类
SessionInfo  Session信息类,包含会话密钥、会话ID等信息
Constant 常量类

```


### API使用示例
```
1. 获取会话：

curl http://localhost:8002/1234/session
响应: 38SB018G (为客户1234创建的会话密钥)
```

2. 提交投注：
```
Invoke-WebRequest `
-Uri "http://localhost:8001/888/stake?sessionkey=38SB018G" `
-Method Post `
-Body "3000"
请求体: 3000 (客户通过会话38SB018G为盘口888提交3000投注额)
```
 
3. 查询最高投注列表：
```
curl http://localhost:8001/888/highstakes
响应: 1239=3000,1238=1000,1237=500,1236=100 (盘口888中客户1239投注3000，
    客户1238投注1000，客户1237投注500，客户1236投注100)
```