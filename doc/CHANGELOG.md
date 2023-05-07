## UnRelease x.x.x
### Added
- function: des
### Change
### Remove
### Fix

### TODO
- 拆分序列化支持库 gson moshi
- jetpack publish 重命名 -ktx
- 多进程exported=false
- 使用AndroidKeyStore来实现AES加密(M+)

## UnRelease 2.0.0
### Added
- 加密接口自定义支持
### Change
- 重构整体结构
- 重构缓存为AtomicCache
### Remove
- 重构Binder合并为一个类
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