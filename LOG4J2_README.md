# Log4j2 日志配置说明

## 📁 日志文件位置

### 主要配置文件
- [log4j2.xml](src/main/resources/log4j2.xml) - Log4j2 核心配置
- [application.yml](src/main/resources/application.yml) - Spring Boot 日志集成配置

---

## 🔧 配置说明

### 1. 日志存储目录配置
在 `log4j2.xml` 第13行：
```xml
<Property name="LOG_HOME">/Users/detective/logs/yto</Property>
```
**说明：** 日志文件保存的根目录，可根据需要修改。

### 2. 日志文件大小配置
在 `log4j2.xml` 第17行：
```xml
<Property name="LOG_FILE_SIZE">100MB</Property>
```
**说明：** 单个日志文件的最大大小，超过后会创建新文件。

### 3. 日志保留天数配置
在 `log4j2.xml` 第19行：
```xml
<Property name="LOG_FILE_RETENTION">30</Property>
```
**说明：** 日志文件保留天数，超过会自动删除。

---

## 📄 日志文件说明

系统会在 `${LOG_HOME}` 目录下生成以下文件：

| 文件名 | 说明 | 用途 |
|--------|------|------|
| `yto.log` | 业务日志 | 所有INFO及以上级别的日志 |
| `yto-error.log` | 错误日志 | 只包含ERROR及以上级别的日志 |
| `yto-sql.log` | SQL日志 | MyBatis SQL语句执行日志 |
| `yto-waybill.log` | 运单日志 | 运单业务相关日志 |

**文件滚动格式：** `yto-2026-05-24-1.log`（日期-序号）

---

## 📊 日志级别说明

| 级别 | 说明 |
|------|------|
| `TRACE` | 最详细的日志信息（一般不用） |
| `DEBUG` | 调试信息（比如SQL语句） |
| `INFO` | 一般信息（默认） |
| `WARN` | 警告信息 |
| `ERROR` | 错误信息 |

---

## 🎨 日志格式

```
2026-05-24 15:30:45.123 [bill-async-1] INFO  [traceId] com.express.yto.service.WaybillDetailService - 开始处理数据
```

**格式说明：**
- `2026-05-24 15:30:45.123` - 时间戳
- `[bill-async-1]` - 线程名
- `INFO` - 日志级别
- `[traceId]` - 追踪ID（可选）
- `com.express.yto.service.WaybillDetailService` - 类名
- `开始处理数据` - 日志内容

---

## 🔍 常用配置修改

### 修改日志级别
在 `log4j2.xml` 第128行修改根日志级别：
```xml
<Root level="INFO">
```
可选值：`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`

### 修改日志格式
在 `log4j2.xml` 第21-23行修改控制台格式：
```xml
<Property name="CONSOLE_LOG_PATTERN">
    %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level [%X{traceId}] %logger{36} - %msg%n
</Property>
```

格式字符说明：
- `%d{pattern}` - 日期时间
- `%t` - 线程名
- `%level` - 日志级别
- `%logger{n}` - 类名（n表示保留n层）
- `%msg` - 日志内容
- `%n` - 换行符

---

## 📝 使用示例

### 在代码中使用日志
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SomeService {
    
    public void doSomething() {
        log.info("开始执行任务");
        log.debug("调试信息");
        log.error("错误信息", exception);
    }
}
```

### 查看日志
```bash
# 实时查看业务日志
tail -f /Users/detective/logs/yto/yto.log

# 查看错误日志
tail -f /Users/detective/logs/yto/yto-error.log

# 查看SQL日志
tail -f /Users/detective/logs/yto/yto-sql.log
```

---

## 🚀 立即生效

**重启应用后，日志配置会自动生效！**

---

## 📌 注意事项

1. 确保日志目录有写入权限
2. 如果修改了配置，需要重启应用
3. 日志文件会按天和大小自动滚动
4. 30天前的日志会自动删除
