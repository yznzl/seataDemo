# Seata 全局事务 Demo

这个 Demo 用两个 MySQL 数据库模拟订单库和库存库。一个 `@GlobalTransactional` 同时写入两库：

- 偶数：订单写入成功、库存减 1，最终提交。
- 奇数：先写订单和库存，再主动抛异常，Seata 回滚两库。
- 不传 `number`：随机生成 0~99，偶数提交、奇数回滚。

## 1. 部署 Nacos 和 Seata

把 `docker/docker-compose.yml` 复制到 `虚拟机路径`，在该目录执行：

```bash
docker compose up -d
docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
```

端口：Nacos `8848`，Seata `8091`。这里 Seata 使用 file 存储，Demo 使用 file registry 指向 `虚拟机ip:8091`；Nacos 负责 Demo 的配置/服务发现，关闭认证以便内网 Demo 直接使用。

## 2. 初始化现有 gh-mysql

在 VM 上执行（把密码替换为现有 MySQL root 密码）：

```bash
docker cp sql/init.sql gh-mysql:/tmp/init.sql
docker exec -i gh-mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < /tmp/init.sql'
```

如果容器没有 `MYSQL_ROOT_PASSWORD` 环境变量，改为直接执行：

```bash
docker exec -i gh-mysql mysql -uroot -p'实际密码' < sql/init.sql
```

## 3. 构建和启动 Demo

```bash
set -a; source .env; set +a
mvn -s maven-settings.xml clean package -DskipTests
DB_HOST=localhost DB_PASSWORD="$DB_PASSWORD" \
  java -jar target/seata-demo-1.0.0.jar
```

Windows 本机使用项目自带的 `maven-settings.xml`，避免被旧的全局 Maven 仓库配置影响；Linux/VM 上可直接使用普通 `mvn clean package -DskipTests`。

## 4. 验证提交和回滚

先看基线：

```bash
curl http://localhost:8081/demo/state
```

偶数提交：

```bash
curl -i 'http://localhost:8081/demo/run?number=2'
```

预期 HTTP 200，`expected` 为 `COMMIT`，订单数增加 1，库存减少 1。

奇数回滚：

```bash
curl -i 'http://localhost:8081/demo/run?number=3'
```

预期 HTTP 500，`expected` 为 `ROLLBACK`；返回的 `stateAfterRollback` 中订单数和库存应与调用前相同。

随机测试：

```bash
curl -i 'http://localhost:8081/demo/run'
```

`mvn test` 和服务器上的两条 curl 验证是最小检查；项目没有额外测试框架或不必要的服务拆分。
