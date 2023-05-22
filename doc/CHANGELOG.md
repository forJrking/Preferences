## UnRelease x.x.x
### Added
- function: des
### Change
### Remove
### Fix

### TODO
- 多进程不能正常获取最新Value
- default 擦除数据安全性, 如果binding的default和修改后的value还是同一个对象仍然无法写入
- 使用AndroidKeyStore来实现AES加密(M+), 加密接口自定义支持  可以借助AI chatgpt
- preferences androidx 实现性能评估

## UnRelease 2.1.0

## Release 2.0.0
### Added
- 加密接口自定义支持
- 拆分序列化支持库 gson
### Change
- 重构整体结构, 更改缓存为AtomicCache
- jetpack publish
### Remove
- 重构Binder合并为一个类
- 重命名bind方法名
### Fix
- data class 修改属性不能正常apply
- 多进程不能正常获取最新Value

## Release 1.0.7
### Change
- 优化getAll过滤非委托属性
- 校验clear方法过滤

## Release 1.0.6
### Added
- 增加getAll: 支持可以迁移数据

## Release 1.0.5
### Added
- 优化性能
### Fix
- 修复因为缓存引起的数据存储问题
- 优化序列化崩溃