# mybatis 缓存机制

Mybatis 系统中默认定义了两级缓存

一级缓存和二级缓存

1. 默认情况下，只有一级缓存（SqlSession 级别的缓存，也称为本地缓存）开启
2. 二级缓存需要手动开启和配置，它是基于 namespace 级别的缓存
3. 为了提高拓展性，Mybatis 定义了缓存接口 Cache，可以通过实现 Cache 接口来自定义二级缓存。

## 一级缓存（本地缓存）

与数据库同一次会话期间查询到的数据会放在本地缓存中，以后如果需要获取相同的数据，直接从缓存中拿。

一级缓存失效情况（没有使用到当前一级缓存的情况，效果就是，还需要再向数据库发出查询）
1. 不同的 SqlSession
2. SqlSession 相同，不同的条件
3. sqlSession 相同，两次查询之间执行了增删改（这次增删改可能对当前数据有影响）
4. sqlSession 相同，手动清除了一级缓存 opensession.clearCache();

## 二级缓存（全局缓存）

基于 namespace 级别的缓存，一个 namespace 对应一个二级缓存

工作机制：

1. 一个会话，查询一条数据，这个数据就会被放到当前会话的一级缓存中
2. 如果会话关闭，一级缓存中的数据会被保存到二级缓存中，新的会话，就可以参照二级缓存中的内容
3. {EmployeeMapper: Employee, DepartmentMapper: Department}
    
不同的 namespace 查出的数据会放在自己对应的缓存中(map)

**使用**
1. 开启二级缓存配置 <setting name="cacheEnabled" value="true">
2. 在 mapper.xml 中配置使用二级缓存 
3. pojo 需要实现序列化接口
        
```xml
<!-- 
    eviction: 缓存的回收策略
        LRU(默认): 最近最少使用，移除最长时间不被使用的对象
        FIFO: 先进先出，按对象进入缓存的顺序来移除它们
        SOFT: 软引用，移除基于垃圾回收器状态和软引用规则的对象
        WEAK: 弱引用，更积极地移除基于垃圾收集器和弱引用规则的对象
    flushInterval: 缓存刷新间隔
        默认不清空，可以设置一个毫秒值
    readOnly: 是否只读
        true: mybatis 认为所有从缓存中获取数据的操作都是只读操作，不会修改数据
            mybatis 为了加快速度，直接就会将数据在缓存中的引用交给用户，不安全，速度快
        false(默认): mybatis 认为获取的数据可能会被修改
            mybatis 会利用序列化&反序列化克隆一份新的数据，安全，速度慢

    size: 缓存存放多少元素
    type: 指定自定义缓存的全类名
 -->
 
<cache eviction="FIFO" flushInterval="60000" readonly="" size="" type=""></cache>
```

查出的数据都会被默认先放在一级缓存中。

只有会话提交或者关闭后，一级缓存中的数据才会转移到二级缓存中

## 和缓存有关系的配置

1. cacheEnabled=true 如果是 false 关闭的是二级缓存，一级缓存不会被关闭
2. 每个 select 标签都有 useCache="true"
    false: 不使用二级缓存(依旧使用一级缓存)
3. 每个增删改标签的 flushCache="true"
    所以增删改会清除缓存，一级缓存就被清空了，二级缓存也会被清空
4. openSession.clearCache() 只是清除当前 session 的一级缓存
5. localCacheScope: 本地缓存作用域(一级缓存)当前会话的所有数据保存在会话缓存中

## 第三方缓存整合：

1. 导入第三方缓存包即可
2. 导入与第三方缓存整合的适配包
3. mapper.xml 中使用自定义的缓存


