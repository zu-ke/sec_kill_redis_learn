## 火车票 抢票 秒杀
* JavaWeb
- Redis
* Redis连接池
- 解决超卖
* lua脚本解决库存遗留问题

### 并发测试
`ab -n 1000 -c 300 -p ~/postfile -T application/x-www-form-urlencoded http://192.168.220.1:8080/seckill/secKillServlet`