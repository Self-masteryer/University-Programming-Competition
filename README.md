# 高校学生编程能力大赛系统



## 一、 项目介绍



###  简介

高校学生编程能力大赛分为**区赛**和**国赛**，并设有**本科组**和**高职组**两个组别，各组别都有六大赛区：西北赛区、西南赛区、东南赛区、东北赛区、东部赛区，中部赛区。本系统主要是用于服务大赛的整个流程，保证各组别赛区比赛正常进行。



***



### 比赛流程介绍



#### 区赛

1. 前期准备：
	* 管理员下发评委、主持人账号（之前有的直接下发，没有的则创建后下发）
	* 管理员设置报名时间

2. 报名环节
	* 学校申请报名后下发学校账号（往届参加过的不用）
	* 学校为学生申请选手账号，参赛人数**不超过3名**。

3. 笔试环节
	* 主持人提前在系统内进行笔试**座位号抽签**
	* 笔试结束后, 需要由**主持人**进行成绩导入
	* 筛选出**前30名**晋级

4. 实战能力比试
	* 主持人对晋级选手进行**分组抽签**（同一组的选手不能来自同一个学校）
	* 每组实战结束后，5位评委对其打分

5. 快问快答环节
	* 本环节选手按环节三中签号依次上场。选手上台的顺序是：A1、B1、A2、B2 …… 15A、15B。
	* 评委对选手进行快问快答, 由**五名**评委对选手回答***打分***
6. 成绩现场导出
	* 比赛结束, 由**主持人**现场导出最终成绩表
	* 筛选前五名进入晋级国赛（若有放弃国赛名额的情况，系统将通知有关选手及时确认顺延递补晋级）



#### 国赛

注：实战能力比试、快问快答环节、成绩现场导出环节跟区赛一致

1. 前期准备
	* 管理员下发评委、主持人账号
	* 管理员开启国赛
2. 实战能力比试
	* 主持人对国赛选手进行**分组抽签**（同一组的选手不能来自同一个学校）
	* 每组实战结束后，5位评委对其打分
3. 快问快答环节
	* 本环节选手按环节三中签号依次上场。选手上台的顺序是：A1、B1、A2、B2 …… 15A、15B。
	* 评委对选手进行快问快答, 由**五名**评委对选手回答***打分***
4. 成绩现场导出
	* 比赛结束, 由**主持人**现场导出最终成绩表



---



###  如何实现比赛按规定的流程进行

在redis中存储每个组别赛区此时的进程，如下图。

![](https://big-event0618.oss-cn-beijing.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202024-06-06%20200848.png)

当调用相关接口时，查询redis，检验是否符合当前进程。具体如何实现请查阅 [CheckProcessAspect](#aspect) 



**流程细分**

1. written：笔试环节

	* seat_draw：座位号抽签
	* post_written_score：上传笔试成绩
	* score_filter：成绩筛选
	* next：主持人推进

2. practice：实战对决环节

	* group_draw：分组抽签
	* rate：评委打分
	* next：主持人推进

3. q_and_a：快问快答环节

	* rate：评委打分
	* next：主持人推进

4. final：最终环节

	* score_export：成绩导出
	* next：完赛

	

**简写介绍**

1. 组别
	* BK:本科
	* GZ:高职
2. 赛区
	* NW：西北赛区
	* SW：西南赛区
	* NE：东北赛区
	* SE：东南赛区
	* C：中部赛区
	* E：东部赛区
	* N：全国



---



### 重要文件



#### controller

处理进入应用的HTTP请求并返回响应



##### AdminController

管理员：

1. **主持比赛前的事件**
	* 下发主持人、评委及学校账号
	* 设置报名时间、开启国赛
	* 修改放弃国赛资格规定时间
	
2. **监控比赛情况**
	* 监控赛区进程
	
	* 监控各账号的状态
	* 监控评委打分情况、选手得分情况
	
3. 备份、还原数据库

注意：管理员拥有主持人的**全部权限**。



##### HostController

主持人：推进比赛

* 开启比赛
* 推进下一环节
* 座位号抽签
* 通过excel上传笔试成绩
* 按笔试成绩筛选
* 分组抽签
* 导出成绩
* 查询每组实战对决分数
* 查询快问快答得分



#####  JudgementController

评委：对选手打分

1. 实战能力比试环节
	* 查询分组选手
	* 对每组选手打分
2. 快问快答环节
	* 查询选手
	* 对选手打分



##### SchoolController

学校：

* 为学生报名
* 查询往届学生获奖情况



#####  ContestantController

选手：

* 查询座位号
* 查询笔试成绩
* 查询组号
* 放弃国赛资格



#####  UserController

用户：

* 登录
* 重置用户名、密码
* 修改密码
* 查询个人往届成绩
* 上传图片
* 查询用户信息
* 修改用户信息



##### SystemMysqlBackupsController

MYSQL数据：

* 查询所有备份数据
* 备份mysql数据
* 恢复mysql数据



---



#### Service



##### AdminService

管理员业务：

1. **下发主持人、评委及学校账号**
	
	1. 管理员提交规定格式的excel文件赋予其角色（权限）
	2. 账号存在，直接赋予其相关角色（权限）。注意：此时“是否重置密码”为必填项
	3. 账号不存在，则创建账号后下发。
	4. 密码：随机获得8位密码，利用BCrypt加密加盐后存储到数据库
	
2. **设置报名时间、修改放弃国赛资格规定时间**：将管理员提交的时间转换为时间戳存储到redis
3. **开启国赛**
	
	1. 根据存储到redis中的一个字段"finish_competition_num"是否等于12判断区赛是否全部结束
	2. 将本科组、高职组相关进程（practice:group_draw）存储到redis中，主持人便可在比赛时开启分组抽签。
	
4. **监控赛区进程**：根据group、zone分页查询，有四种情况
	
	* group、zone都不输入：查询全部
	
	* 输入group：查询组别的全部赛区进程
	* 输入zone：查询地区的全部组别进程
	* 都输入：具体查询某个组别赛区的进程
5. **监控各账号的状态**：直接分页查询
6. **监控评委打分情况、选手得分情况**：根据选手名、评委名、组别、赛区分页查询
7. **设置为游客身份**：根据组别、赛区将账号设置为游客角色（除晋级国赛选手）



##### HostService

1. **开启比赛**

	* 首先根据存储在redis中的报名时间，判断报名是否已结束
	* 再根据redis中是否有本组别赛区进程信息，判断比赛是否已经进行
	* 将本组别赛区进程信息（written:seat_draw）存储到redis

2. **推进下一环节**

  * 从redis获得本组别赛区进程信息，检验环节步骤是否为next
  * 获得下一进程存储到redis
  * 如果step为rate，开启每一小时自动备份数据库

3. **座位号抽签**

	* 读取组别赛区选手总数
	* 创建一个1~n的List
	* 利用Collections.shuffle()打乱List
	* 按顺序给选手分配座位号

4. **通过excel上传笔试成绩**：直接按格式读取成绩数据，存储到mysql

5. **按笔试成绩筛选**

  1. 查询成绩单后按笔试成绩降序排序
  2. 将笔试成绩存储到mysql
  3. 从contestant表中删除淘汰选手
  4. 从score_info表中删除淘汰选手的成绩单

6. **分组抽签**

	1. 首先创建一个映射来跟踪每个学校的选手

	2. 此时还有一个List<Student> students存储未分组的学生
	3. 循环直到students列表的大小等于6
	4. 首先从students取出学生A，再查询schoolStudents与之不同的学校，从中取出学生B组成一组
	5. 剩下6名选手随机分组可能发生同校同组的情况，需要回退
	6. 按之前的逻辑进出循环，当剩下两名选手来自同一学校时，回退两步
	7. 考虑到后面评委要频繁查询分组名单，所以将分组信息存进redis
	8. 更新成绩单分组信息
	
	附分组抽签算法
	
	```java
	    public List<SignGroup> groupDraw(List<Student> students) {
	        // 创建一个映射来跟踪每个学校的选手
	        Map<String, List<Student>> schoolStudents = new HashMap<>();
	        for (Student student : students) {
	            schoolStudents.computeIfAbsent(student.getSchool(), k -> new ArrayList<>()).add(student);
	        }
	        int signNum = 1;
	        Collections.shuffle(students);// 打乱选手顺序
	        List<SignGroup> signGroups = new ArrayList<>();// 存放分组信息
	        while (students.size() > 6) {
	            Student A = students.remove(0);
	            schoolStudents.get(A.getSchool()).remove(A);
	            for (String school : new ArrayList<>(schoolStudents.keySet())) {
	                if (!schoolStudents.get(school).isEmpty() && !Objects.equals(school, A.getSchool())) {
	                    Student B = schoolStudents.get(school).remove(0);
	                    students.remove(B);
	                    signGroups.add(new SignGroup(signNum++, A, B));
	                    break;
	                }
	            }
	        }
	        // 剩下6名选手随机分组可能发生同校同组的情况，需要回退
	        Stack<Student> stack = new Stack<>();
	        while (!students.isEmpty()) {
	            Student A = students.remove(0);
	            schoolStudents.get(A.getSchool()).remove(A);
	            stack.push(A);
	            boolean flag = true;
	            for (String school : new ArrayList<>(schoolStudents.keySet())) {
	                if (!schoolStudents.get(school).isEmpty() && !Objects.equals(school, A.getSchool())) {
	                    Student B = schoolStudents.get(school).remove(0);
	                    students.remove(B);
	                    stack.push(B);
	                    signGroups.add(new SignGroup(signNum++, A, B));
	                    flag = false;
	                    break;
	                }
	            }
	            // 剩下两名选手来自同一学校，回退两步
	            if (flag) {
	                students.addAll(stack);
	                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
	                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
	                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
	                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
	                signGroups.remove(signGroups.size() - 1);
	                signGroups.remove(signGroups.size() - 1);
	                signNum -= 2;
	            }
	        }
	        return signGroups;
	    }
	```
	
	
	
7. **导出成绩**

  1. 按降序排序最终成绩
  2. 根据组别区分区赛和国赛，采用不用的成绩导出模板
  3. 使用iTextPdf为Pdf模板填充表单项

8. **查询每组实战对决分数**：根据uid查询

9. **查询快问快答得分**：根据uid查询



##### JudgementService

1. **查询分组选手**
  1. 检验查询范围是否异常
  2. 从redis中查询分组信息json字符串
  3. 解析为列表后返回
2. **对每组选手打分**
  1. 检验是否重复打分
  2. 插入选手得分信息
  3. 判断管理员是否在监控打分，在则发送评委打分信息
  4. 将rate_times（几位评委已打分）+1后存储到redis
  5. 判断五位评委是否打分完毕
          1. 计算平均分后插入选手成绩单
             2. 判断管理员是否在监控选手得分，在则发送选手得分信息
                   3. 删除rate_times
                       4. 将rate_nums（已打分选手）+1后存储到redis
                           5. 判断30位选手是否全部打分完毕
        2. 删除rate_nums
        3. 推进流程至next
        4. 关闭每一小时自动没法数据库，开启每天00：00自动备份数据库
        5. 删除存储在redis中的分组签号信息
        6. 存储个人签号
      
3. **查询选手**：同“查询分组选手”一样的逻辑
4. **对选手打分**：同“对每组选手打分”的逻辑，**不同点**在于最后一步只删除了“个人签号”



##### ContestantService

* **查询座位号、组号**：直接按uid查询

* **放弃国赛资格**
	1. 检验是否为国赛选手
	2. 校验是否在规定的时间内
	3. 从contestant表中删除放弃国赛资格的选手信息
	4. 从redis中获得顺延递补的选手排名序号
	5. 保存下一位排名序号
	6. 添加下一位选手信息

* **删除未晋级选手**：将排序低于5的选手从contestant表中删除



##### UserService

* **登录**
	1. 通过用户名查询账号，检验账号是否存在、启用
	2. 校验密码
	3. 利用satoken登录
	4. 将group、zone、role存储到sasession中，方便后续直接调用

* **重置用户名、密码**
	1. 检查能否修改用户名（一年只能修改一次）
	2. 检查用户名是否已存在
	3. 检查两次密码是否一致
	4. 强制退出，重新登录

* **修改密码**
	1. 检验旧密码是否正确
	2. 检验新密码是否一致
	3. 强制退出，重新登录

* **查询用户信息**：直接按uid查询

* **修改用户信息**：直接修改



##### ScoreService

* **添加往届成绩**：按group、zone将成绩单存储到pre_score中

* **查询往届成绩（管理员）**：可按姓名、组别、赛区、届数、排名分页查询

* **查询个人往届成绩**：直接按uid查询

* **计算最终成绩**
	1. 判断是区赛还是国赛
	
	2. 按规定的比例计算最终成绩
	
* **添加学生成绩**
	1. 查询学生成绩
	2. 按成绩降序排序
	3. 判断区赛还是国赛
	4. 第1、2名设置一等奖、第3~5名设置二等奖

* **查询学生成绩**：可按姓名、学校、届数、奖项分页查询
* **查询笔试成绩**：直接按uid查询
* **删除笔试成绩**：按group、zone删除
* **删除成绩单**：按group、zone删除成绩单、实战对决成绩、快问快打成绩



##### StpInterface

sakoken自定义权限加载接口实现类

1. 返回一个账号所拥有的权限码集合：直接根据uid查询permission表
2. 返回一个账号所拥有的角色标识集合：直接根据uid查询role表



##### SystemMysqlBackupsService

* **查询备份数据**：根据id查询

* **查询所有备份数据**

* **备份mysql数据库**
	1. 从url中获得ip、端口号
	1. 获得数据库文件名称
	1. 获得备份命令
	1. 判断文件路径是否存在，不存在就创建
	1. 备份信息存放到数据库
	1. 获取Runtime实例，备份数据库

* **恢复数据库**

	1.  获得备份路径文件名

	2. 获得恢复命令

	3. 获取Runtime实例，恢复数据库



#### common



##### constant

* ErrorMessage：错误信息常量

* Group：组别常量
* Zone：赛区常量
* Process：进程常量
* Step：步骤常量
* Item：项目常量
* Prize：奖项常量
* Role：角色常量
* Supervise：监控常量
* Time：时间常量



##### exception

各种异常



##### json

JacksonObjectMapper对象映射器

* 基于jackson将Java对象转为json
* 将json转为Java对象



##### properties

* AliOssProperties：阿里云属性

* MysqlProperties：mysql属性



##### result

* PageResult：分页查询结果
* Result：http相应结果



##### util

* **AliOssUtil**：上传文件
* **ConvertUtil**：转换工具包
	* 角色中文---->角色数字
	* 角色数字、数字字符---->角色中文
	* 地区中文---->地区缩写英语单词
	* 地区缩写英语单词---->地区中文
	* 组别中文---->组别缩写英语单词
	* 组别缩写英语单词---->组别中文
	* 进程英语---->进程中文
	* 步骤英语---->步骤中文
	* 字符串时间戳---->LocalDateTime
	* LocalDateTime---->字符串时间戳
	* 状态数字字符---->状态中文
	* 分组签号---->序号

* **RandomStringUtils**：获得长度为n包含大小写英文、数字及".$@!%*?&"的随机字符串（特殊字符在末尾一位）
* **RedisUtil**：获得各种键名



#### annotation

* AfterCompetition
	* 切面AfterCompetitionAspect的注解

* CheckProcess
  * 切面CheckProcessAspect的注解
  * 参数
  	* process：进程
  	* step：步骤

* CheckQueryProcess、
  * 切面CheckQueryProcessAspect的注解
  * 参数
  	* process：进程
  	* item：查询项目



#### aspect

* **AfterCompetitionAspect**：比赛结束后对数据处理，用于导出成绩接口处
	1. 判断是主持人还是管理员调用导出成绩接口
	2. 完赛数量加一
	3. 将成绩存储到学校的学生成绩
	4. 将成绩存储到往届成绩
	5. 删除成绩信息
	6. 判断区赛还是决赛
	7. 区赛
		1. 从contestant中删除未晋级选手
		2. 将选手、笔试阶段淘汰的选手、主持人、评委设置为游客身份
		3. 删除笔试成绩
		4. 设置默认放弃国赛资格时间段 
	
	8. 国赛
		1. 从contestant中删除全部选手
		2. 将选手、主持人和评委设置为游客身份
		3. 删除本组本赛区redis进程信息
		4. 国赛结束
			1. 备份数据
			2. 关闭数据库自动备份
			3. 删除redis数据
	
* **CheckProcessAspect**：
  
  1. Before：检验调用接口时是否符合当前进程
  
  	1. 获取方法签名
  
  	2. 获得注解参数（process、step）
  
  	3. 判断是管理员还是主持人或评委
  	4. 设置group、zone
  	5. 匹配redis中的进程
  
  		* 匹配：放行
  
  		* 不匹配：拦截
  
  
  2. After：自动跳转到下一step（步骤）

  	1. 判断是否为打分环节，是则return
  	2. 获得下一步骤
  	3. 更新进程信息
  	4. 如果step为rate，开启每一小时自动备份数据库
  
* **CheckQueryProcessAspect**：检验调用接口时是否符合当前进程
1. 获取方法签名
	
2. 获得注解参数（process、item）
	
3. 获得flag标记
	
	* 判断进程
	
		1. 笔试环节：
				1. 检验是否为区赛,不为区赛则抛异常
				2. item等于score---->flag=3
				3. item等于num---->flag=1
	
		2. 实战对决环节：flag=5
			3. 快问快答环节：flag=7
	
4. 获得当前进程
	
5. 遍历processStep字符串数组
	
	* <flag时查询到，表示未到能查询的进程



**附processStep字符串数组**

0. written:seat_draw
1. written:post_written_score
2. written:score_filter
3. written:next
4. practice:group_draw
5. practice:rate
6. practice:next
7. q_and_a:rate
8. q_and_a:next
9. final:score_export
10. final:next



#### config

* **OssConfiguration**
	* 注册AliOssUtil
* **ThreadPoolConfiguration**
	* 配置ThreadPoolTaskExecutor，适用于复杂异步任务处理
* **ThreadPoolTaskSchedulerConfig**
	* 配置ThreadPoolTaskScheduler，专注于定时任务调度
* **TopicRabbitMQConfig**：配置主题消息队列
	* 消息队列
		* 用户状态消息队列
		* 评委打分消息队列
	* 交换机：主题交换机
	* 捆绑：通过路由键捆绑主题交换机与消息队列
* **WebMvcConfiguration**
	* 注册 Sa-Token 路由拦截器
	* 拓展spring mvc框架的消息转换器
* **WebSocketConfiguration**
	* 注册user端点
	* 注册superviseStatus端点
	* 注册superviseRate端点



#### interceptor

* **UserWebsocketHandshakeInterceptor**
	* 解析token，检验是否登录
	* 将uid存储到attributes
* **AdminWebSocketHandshakeInterceptor**
	* 在UserWebsocketHandshakeInterceptor的基础上添加管理员身份验证



#### handler

* **GlobalExceptionHandler**：全局异常处理者
* **UserWebSocketHandler**
	* **连接建立**
		1. 更新用户状态为在线
		
		2. 判断管理员是否在监视账号状态
			1. 构建json状态信息
			2. 向消息队列发送用户在线消息
		
		3. 保存webSocketSession
		
	* **关闭连接**
	1. 更新用户状态为离线
		
	2. 判断管理员是否在监视账号状态
			1. 构建json状态信息
			2. 向消息队列发送用户离线消息
		
	3. 移除webSocketSession
* **SuperviseStatusWebSocketHandler**
  * 连接建立：开启向用户状态消息队列发送状态消息
  * 关闭连接：关闭向用户状态消息队列发送状态消息
  * 发送用户状态信息

* **SuperviseRateWebSocketHandler**
  * 连接建立：开启向评委打分消息队列发送打分消息
  * 关闭连接：关闭向评委打分消息队列发送打分消息
  * 发送评委打分或选手得分信息



#### rabbitMQ

* **consumer**
	* **StatusInfoQueueReceiver**
		* 监听用户状态消息队列
		* 判断管理员是否在监控
			* 发送用户状态信息
	
	* **RateInfoQueueReceiver**
		* 监听评委打分消息队列
		* 判断管理员是否在监控
			* 发送评委打分信息
	
* **provider**



#### taskSchedule

* **AutoBackupsService**
	* 启动自动备份
	* 关闭自动备份
* **DeleteExpireData**
	* 每天00：00：00删除过期数据



## 二、 使用的技术栈



* **Spring Boot**

	* **简介**：基于Spring框架，通过提供一系列默认配置和简化应用配置的方式，极大地降低了新Spring应用的搭建时间和复杂度。Spring Boot强调“约定优于配置”，使得开发者能够快速启动和运行应用，无需过多关注底层基础设施的配置。
	* **在项目中的角色**：扮演核心框架的角色。它负责整合各种技术组件，如数据库访问、Web服务、安全性配置等，形成了一个统一、协调的应用开发环境。通过Spring Boot，快速构建RESTful API、实现数据库访问、集成安全框架等，且所有这些操作都几乎无需手动编写XML配置。
	* **技术亮点**：
		- **快速启动**：内嵌式容器使得应用可以直接运行，无需部署到外部服务器。
		- **自动配置**：根据引入的依赖自动配置Spring组件，减少手动配置。
		- **Starter POMs**：简化依赖管理，一键导入所需功能模块。

	

* **MySQL**

	* **简介**：一款开源的关系型数据库管理系统。
	* **在项目中的角色**：作为主要的数据持久化存储，负责存储和管理应用的所有结构化数据。
	* **技术亮点**
		* 利用事务处理保证数据完整性。
		* 备份与恢复机制确保数据安全。

	

* **Redis**
	* **简介**：一款开源的、基于键值对的数据结构存储系统，同时也可用作数据库、缓存和消息代理。
	* **在项目中的角色**：提高数据访问速度和处理高频访问数据，如热点数据缓存、消息队列等。
	* **技术亮点**：
		* 高速内存存储降低延迟。
		* 丰富的数据结构满足多样需求。



* **SaToken**

	* **简介**：一个轻量级的Java权限认证框架，专注于登录认证和权限控制。
	* **在项目中的角色**：负责实现用户的登录认证和权限验证功能，确保每个用户仅能访问其授权范围内的资源。
	* **技术亮点**
		* 无状态认证设计减轻服务器负担。
		* 易于集成，快速接入。

	

* **Websocket**

	* **简介**： 在单个TCP连接上提供全双工通信的协议，它使得服务端和客户端能够实时、高效地进行双向数据传输。WebSocket解决了传统HTTP协议的短连接问题。
	* **在项目中的角色**： 主要用于实现即时通讯功能，如管理员实时监控账号状态、评委打分、选手得分情况。
	* **技术亮点**：
		- **全双工通信**：同时支持服务端向客户端和客户端向服务端发送数据。
		- **低延迟**：减少不必要的网络往返，提高数据传输效率。
		- **持久连接**：一次握手后保持连接，减少通信开销。
		- **广泛支持**：现代浏览器和后端框架普遍支持WebSocket。

	

* **RabbitMQ**

	* **简介**： 一款开源的消息队列系统，基于AMQP（Advanced Message Queuing Protocol）协议，用于在分布式系统中解耦应用程序的各个部分，实现异步消息传递。它确保消息的可靠传递，支持多种消息模式，如点对点、发布/订阅等。

		**在项目中的角色**： 主要负责消息的中间传递和解耦，实现处理高并发场景下的消息堆积问题。

		**技术亮点**：

		- **高可用性**：支持集群部署，确保消息不丢失。
		- **多种消息模式**：支持多种消息传递模型，满足不同场景需求。
		- **消息确认机制**：确保消息被正确处理。
		- **管理界面**：提供直观的Web管理界面，便于监控和管理队列。

	

* **Prometheus**

	* **简介**： 一款开源的监控和警报系统。它专为云原生环境设计，擅长收集、聚合、存储和展示时间序列数据，特别适合用于监控微服务架构、容器化应用以及大型分布式系统。
	* **在项目中的角色**：
		- **数据采集**：通过Prometheus Server主动拉取或Exporter被动推送的方式，从各类目标（如应用、数据库、硬件等）收集性能指标。
		- **存储管理**：将收集到的数据存储在本地的时序数据库中，支持高效查询和长期存储。
		- **告警生成**：根据预定义的规则评估指标数据，触发告警通知，及时发现并响应系统异常。
		- **数据可视化**：虽然Prometheus本身提供表达式浏览器查询数据，但通常与Grafana等可视化工具集成，以实现更丰富的数据展示。
	* **技术亮点**：
		- **灵活地查询语言**：PromQL，一种强大且灵活的查询语言，便于进行复杂的数据筛选和聚合分析。
		- **多维度数据模型**：支持标签（labels）来区分和组织数据，便于细粒度监控和灵活查询。
		- **可扩展性**：通过Exporters和Service Discovery机制，轻松集成多种服务和系统的监控。
		- **社区支持**：拥有活跃的开源社区和丰富的生态，不断有新的Exporter和集成方案出现。

	

* **Grafana**
	* **简介**：一款开源的数据可视化和分析平台，它能够展示来自多种数据源（包括Prometheus）的时间序列数据，并提供强大的仪表板构建功能。Grafana以其美观的界面和高度可定制性闻名，广泛应用于监控系统、数据分析和业务智能领域。
	* **在项目中的角色**： 与Prometheus结合时，Grafana在项目中扮演的角色主要包括：
		- **数据可视化**：将Prometheus收集的监控数据转化为图表、仪表盘，直观展示系统状态和性能指标。
		- **交互式分析**：提供丰富的查询选项和过滤条件，支持用户交互式探索数据，便于故障排查和性能优化。
	* **技术亮点**
		* **丰富的数据源支持**：除了Prometheus，还支持InfluxDB、MySQL、Elasticsearch等多种数据源。
		* **动态仪表板**：可使用模板变量创建动态更新的仪表板，适应不同的监控场景和视角。
		* **告警通知渠道多样**：支持电子邮件、Slack、PagerDuty等多种通知渠道。
		* **社区插件**：拥有庞大的插件市场，支持额外的数据可视化选项、数据处理函数等，极大丰富了功能。



## 三、项目亮点



### 流程设计

详细划分每个环节有哪些步骤，确保比赛有条不紊地正常地进行。详细内容请见下图。

[如何实现比赛按规定的流程进行](#如何实现比赛按规定的流程进行)在项目介绍时已说明

![](https://big-event0618.oss-cn-beijing.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202024-06-07%20163152.png)



---



### 基于Websocket和RabbitMQ实现管理员监控后台



**思路**：管理员要监控后台，就需要获得实时信息。当发生相关事件时，首先需要服务器判断管理员是否开启监控，开启就主动向浏览器发送变化的信息，前端收到后展示信息。这就需要用到websocket全双工通信协议了。



**实现**：

那么服务器如何判断管理员是否开启监控呢？

很简单，在redis中存储一个键值对，键名为监控的事件，值表示是否开启监控。

键名如图所示，值为 “1” 表示开启；值为 “0” 表示关闭

![](https://big-event0618.oss-cn-beijing.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202024-06-07%20192441.png)

**流程**

1. 管理员调用相关接口，建立websocket连接
2. 在afterConnectionEstablished方法里开启监控（将值设置为“1”） [见handler](#handler)
3. 当发生相关事件时，程序会从redis中取出相关的值，判断是否开启监控
	* 开启：将实时信息推送到相关的消息队列
	* 关闭：忽视实时信息
4. 消息队列接受到实时信息，同样也要判断是否开启监控  [见rabbitMQ](#rabbitMQ)
	* 开启：将实时信息通过websocket发送到浏览器
	* 关闭：丢弃实时信息
5. 前端接收后展示实时信息
6. 管理员断开websocket连接
7. 在afterConnectionClosed方法里关闭监控（将值设置为“0”）



**为什么使用消息队列？**

1. **解耦系统组件**：通过消息队列，实时信息的检测逻辑（生产者）与监控系统（消费者）被解耦。这意味着即使监控系统暂时不可用或者进行维护，也不会影响到用户在线状态的检测流程，提高了系统的灵活性和稳定性。
2. **异步处理**：消息队列允许异步处理消息，即实时信息可以立即放入队列，而监控系统可以按自己的节奏消费这些消息，无需等待即时响应。这样减少了用户操作与后台处理间的延迟，提升了用户体验。
3. **削峰填谷**：在高并发场景下，可能会瞬间产生大量请求。消息队列能作为缓冲区，平滑处理高峰期的流量，避免直接冲击到监控系统，有助于维持系统稳定运行。
4. **可扩展性**：随着系统规模的增长，监控需求也可能变得更加复杂，消息队列可以很容易地扩展以处理更大的消息吞吐量，只需增加消息队列的实例或调整队列配置，而不需要修改监控系统的代码。
5. **可靠性保障**：大多数消息队列服务提供持久化和高可用性配置，即使在部分系统组件故障的情况下也能保证消息不丢失，从而确保监控数据的完整性，这对于分析用户行为和系统稳定性至关重要。
6. **灵活的消息路由**：消息队列如RabbitMQ支持复杂的路由规则，可以将用户在线状态的变化按照不同的规则分发给不同的监控模块或者备份系统，便于实现更精细化的监控和管理。
7. **监控与报警**：结合消息队列，可以更容易地实现对监控系统的自身健康状况的监控，例如设置警报当队列积压过多消息时，提前发现并解决问题。



---



### 自动备份、删除过期数据库、恢复数据库

如果比赛过程中,某个地方宕机了，要快速恢复比赛，就需要恢复数据库，此时就需要定期备份数据库了。

由于一年中比赛的时间就那么点，于是我采取了利用taskScheduler开启、关闭自动备份数据库。并且在评委打分环节时设置每一个小时自动备份一次数据库；其余环节，每天00：00自动备份数据库。[见taskSchedule](#taskSchedule)

我还提供了相关接口供管理员手动备份数据库（当收到告警时，可备份最新数据库）、恢复数据库。[见SystemMysqlBackupsService](#SystemMysqlBackupsService)



---



### 使用Prometheus+Grafana监控服务器各项指标

为了避免和减少服务器宕机的造成损失，我入门了Prometheus+Grafana，用他们监控服务器各项指标，当指标异常时，及时发送告警通知相关人员。



## 四、学习心得



### 新技术



#### SaToken

之前都是用JWT自己写登录认证，也没接触过多角色鉴权。通过这次的考核，让我了解到了SaToken这款国产开源轻量级的Java权限认证框架。令我印象最深的是他是国产的。之前用的开源框架的官方文档都是用英语编写的，对于我这种英语不好的人非常不友好，以至于我都是在网上查阅资料学习使用。但这次，SaToken却给了我不一样的体验（看中文官方文档好爽啊）。

但是由于对cookie的不了解，同时之前也没用过cookie自动提交token值（以前都是在请求头添加token值），因此在刚开始学习使用SaToken进行登录验证的时候，我就遇到了问题。我手动添加token的值到请求头（不想每次测试接口都登录），误认为系统关闭了，下次系统开启时，token的值仍然有效（之前用JWT自己写登录认证的时候就是这样的）。但事实是token失效了，登录认证失败。在查阅了相关资料后，我知道了SaToken在登录认证后为这个账号创建了一个Token凭证，且通过 Cookie 上下文返回给了前端。apifox就会自动保存token，调用其他接口时，就不用手动添加token值了。

这次的考核项目，我只用了SaToken的登录认证和角色鉴权（非常的方便），他还有好多令我好奇的功能等待着我去了解。希望学长能分享一下其他经常用到的功能。



####  Websocket

之所以学习使用websocket是为了实现记录用户最后一次在线时间和管理员实时监控用户状态、评委打分的业务。

在刚开始学习使用Websocket时我同样遇到了问题。我一开始采用的是基于注解的方式 (@ServerEndpoint)实现websocket，但是却访问不到websocket的端点，在查阅了资料后，仍然无法解决。于是我只好采用更加复杂的实现WebSocketHandler接口的方式实现websocket，这次的实现非常的顺利，没有出差错。

顺利连接上了websocket端点，新的问题又随之出现。如何登录认证和角色鉴权呢？？？首先我询问AI能否用SaToken对ws协议进行认证鉴权呢，答案是否定的：SaToken主要用于处理HTTP请求的认证与授权。由于想偷懒（不想每次发送websocket连接，都手动添加token），于是我尝试不添加token，看看apifox能否自动发送cookie，结果也是否定的。于是我只好老老实实在发送websocket连接请求时填写token，然后在beforeHandshake方法中解析token进行手动登录认证。

websocket的用处远不止此，我迫切地想学习他的其他用处，但奈何时间不均须。暑假有机会再学习其他用处吧。



#### RabbitMQ

为了解耦用户状态、评委打分、和选手得分信息改变和发送WebSocket消息发送之间的逻辑及平滑处理高峰期的流量，避免直接冲击到监控系统，我使用了消息队列作为中间件。这样，即使有很多信息同时更新，也不会对WebSocket服务器造成过大压力。

RabbitMQ的入门学习倒是非常的顺利，除了配置环境花了点时间外，其余都是一气呵成。

当然RabbitMQ我也只是刚刚入门，还有好多功能等待着我去学习。毕竟学无止境嘛，不能满足于当下，与时俱进才是出路！！！



#### Prometheus+Grafana

为了监控服务器及应用的各项指标，当系统指标异常时，及时发送告警通知相关人员，我入门了Prometheus和Grafana。

这其中也遇到了不少困难。由于我查找到的那篇资料是介绍监控linux系统的（下载的exporter为node_exporter），于是我不得不再找一篇安装wmi exporter的资料并与之整合。最大的问题是要被监控的ip地址的填写，资料上演示的ip地址是以10开头的（我之前不知道这是本地ip地址），我就填写的localhost或127.0.0.1。结果就是连接不上，我以为是我配置写错了，检查半天也没找出原因。只好在网上搜索别的资料，发现要填写本地ip地址，才能连接的上。



---



### 不足

1. **数据库建表考虑不周**：经常在编写业务逻辑时，更改表的字段

2. **实体类命名杂乱**：有时候不知道该如何命名
3. **项目经验不足**：有些结构不是很规范、不知道哪里需要解耦



---



### 给学长的留言

本次所编写的系统是我凭感觉写的，希望学长能指出不足，我好做出改进。也渴望与学长进行交流，对我后续的学习进行指导（如果有空的话）。



---



### 总结

本次的项目考核，让我受益匪浅。不仅让我学到了许多的东西，比如新的技术栈，也巩固我对.md文件的编写能力，同时也激发了我对新技术的渴望，希望用他们编写出一个成熟的的系统（好奇长啥样，没了解过）。我很享受学习完一项新技术后的充实感（学习过程有点痛苦）。我知道我在java后端方向的学习才刚开始起步，不过我会坚持下去的，写一个属于自己的网站超酷的，好吧！
