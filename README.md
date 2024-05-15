## 项目地址

GitLab-git : [git@47.108.80.81:zhuyongdong/ulla-option.git](git@47.108.80.81:zhuyongdong/ulla-option.git)

GitLab-http: [http://47.108.80.81:5360/zhuyongdong/ulla-option.git](http://47.108.80.81:5360/zhuyongdong/ulla-option.git)

## 后端开发规范

#### 书写规范

- 代码写到dev分支,提测合并到sit,测试通过后发布到master
- 接口代码位置写在模块的controller下
- 逻辑、模型、映射写在framework模块下,需要自行创建对应的功能目录
- 无需要编写xml文件

默认规约 与阿里巴巴java规约一致，如果有需要，可以在idea中以及类似ide中下载对应的阿里巴巴规约

[Java开发手册（华山版）.pdf](https://docs.pickmall.cn/development/images/Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%EF%BC%88%E5%8D%8E%E5%B1%B1%E7%89%88%EF%BC%89.pdf)

### 规范扩展

+ 类名使用 UpperCamelCase 风格：第一个词的首字母，以及后面每个词的首字母都大写
+ 方法名使用lowerCamelCase风格：第一个词的首字母小写，后面每个词的首字母大写。
+ 常量命名全部大写，单词间用下划线隔开。

### 类注释模板

```
 /**
 * @Description {类内容}
 * @author {作者}
 * @since {date} {time}
 */
```

### 开发规范

1. 控制器需以:Controller结尾，例如：BlockController。
    1. 控制器注解引入
        - @RestController：标注类为Restful规范的控制器。
        - @RequestMapping：请求路径，需遵守Restful规范。
        - @RequiredArgsConstructor(onConstructor = @__(@Autowired))：
    2. 设定请求方式
        - 查询方法：@GetMapping
        - 添加：@PostMapping
        - 修改：@PutMapping
        - 删除：@DeleteMapping
        - **不允许使用：@RequestMapping(method =XXX)，例如：@RequestMapping(value = "/token", method = RequestMethod.GET)**
    3. 入参设计
        - 入参需以lowerCamelCase风格命名。
    4. 设定请求参数方式
        - 表单提交：直接使用VO类或具体参数名接收。
        - @PathVariable：路径变量。
        - @RequestParam：参数请求。
    5. 校验请求参数
        - 非空：@NotNull(message = "XXX")。
        - 使用注解@Validated，使得参数自动校验生效，它是spring-contex中的注解。
        - 对象可在变量上进行定义。
    6. 方法返回
        - 查询、修改、添加返回：操作后的对象。
        - 其他业务操作返回：操作状态。
            + 返回对象：ResultUtil.data(返回对象);
            + 操作成功：ResultUtil.success("返回内容");
            + 操作失败：ResultUtil.error("返回内容");
    7. 自动生成接口文档
        - 使用SwaggerAPI。
        - @Api()：添加在类名之上，标注控制器实现内容。
        - @ApiOperation(value = "XX")：添加方法名上，标注方法实现内容。
        - @ApiImplicitParams、@ApiImplicitParam：添加方法名上，标注参数内容。
2. 业务层需以:Service结尾，例如：BlockService。
3. Mapper需以:Mapper结尾，例如：BlockMapper。
4. 模型命名规范
    + 在mo 目录下的类，即为数据库模型，默认后缀Mo命名，例如BlockMo
    + 在vo 目录下的类，即为响应数据模型，默认后缀Vo命名，例如BlockVo，通常情况用于服务端给前端的响应
    + 在qo 目录下的类，即为传递数据模型，默认后缀Qo命名，例如BlockQo，通常情况用于前后端，或者服务于服务之间交互的数据模型

## framework模块目录示例

```
modules
├─admin
│   ├─qo
│   │      MsgQo.java
│   ├─mo
│   │      MsgMo.java
│   ├─mapper
│   │      WebSocketLoginMapper.java
│   ├─service
│   │   │  WebSocketLoginService.java
│   │   └─impl
│   │           WebSocketLoginServiceImpl.java
│   └─vo
│          MsgVo.java 
└─business
    ├─qo
    │      MsgQo.java
    ├─mo
    │      MsgMo.java
    ├─mapper
    │      WebSocketLoginMapper.java
    ├─service
    │   │  WebSocketLoginService.java
    │   └─impl
    │           WebSocketLoginServiceImpl.java
    └─vo
           MsgVo.java 
```

## 功能模块

| 模块          | 说明         |
|-------------|------------|
| admin-api   | 后面管理模块     |
| auth-api    | 用户权限模块     |
| framework   | 架构/ 核心代码   |
| gateway     | 防火墙        |
| pc-api      | 桌面端接口模块    |
| android-api | 安卓端接口模块    |
| ios-api     | 苹果端接口模块    |
| ulla-api    | 乌拉钱包接口模块   |
| test-model  | 接口加密测试demo |
| xxl-job     | 定时器        |

## 后台技术选型

| 说明           | 框架                         |
|--------------|----------------------------|
| 基础框架         | Spring Boot                |
| 持久框架         | Mybatis-Plus               |
| 安全框架         | Spring Security            |
| 程序构建         | Maven                      |
| MVC框架        | Spring MVC                 |
| 关系型数据库       | MySQL                      |
| 缓存           | Redis                      |
| 定时任务         | xxl-job                    |
| 接口加密         | requestId、timestamp、ras    |
