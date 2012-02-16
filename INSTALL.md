# h2weibo 架设指南

## 引子

几个月前，为了让我的 Twitter TL 能够自动的同步到围脖，[我](http://jyorr.com) ([@xu_lele](http://twitter.com/xu_lele)) 写一小段脚本。写着写着我觉得可以利用 cloudfoundry.com 提供的免费服务器资源，提供一个给更多人使用的工具，于是在经过一个昼夜的奋斗后 h2weibo 诞生了。

经过很多朋友的使用和不断的代码改进后，h2weibo 已经能够在一台 256M 内存的服务器中为 1600 名左右的朋友提供 2 分钟以内的 TL 同步。然而好事多磨，新浪突然来信说 h2weibo 违反了用户协议，将 h2weibo 应用降级为未审核应用。*此处删去1094字。*

从技术上讲，围脖的未审核应用只能允许 10 人左右的极少数用户使用，所以 h2weibo 作为一个为朋友们提供服务的应用，在成为未审核应用后就没得玩了。但是作为个人用或小范围使用的应用，未审核也没任何问题。

而这篇指南就是为有兴趣尝试搭建一个自己的 h2weibo 服务的朋友写的。

## Step.0 前提条件

1. 服务器架设在 cloudfoundry.com，这是 VMWare 提供的免费云服务。需要架设的同学需要先申请一个账号。
2. cloudfoundy 没有 GUI 的界面管理应用，需要使用一个叫做 vmc 的命令来上传应用。这个命令是 ruby 写的，所以需要你的本地有 ruby 的运行环境和 gem (ruby 的包管理软件)
3. 代码用 Java 书写，至少需要有一个能够编译 Java 代码的环境。代码管理和编译使用的 Apache Maven2 也必须有。

如果看到上面三项已经让你觉得头疼，很好，你可以到此收手，试试看别的同步应用。

另：如果安装中碰到了问题可以先看看下面的反馈信息，如不能解决可以 [@xu_lele](http://twitter.com/xu_lele) 直接问我。


## Step.1 创建应用

为了完成同步，首先需要在围脖和 Twitter 各创建一个应用，一个负责取数据，一个负责写数据。

* 在 https://dev.twitter.com/ 创建一个新应用，取得 Consumer key 和 Consumer Secret
* 在 http://open.weibo.com/ 创建一个网站。名称地址介绍等都可以随便填，域名绑定选择否。完成后看应用信息可以看到 App Key 和 App Secret
* 这四个畸形字符串填入项目根目录下的 default.properties

## Step.2 编译 h2weibo

首先要获得 h2weibo 的源代码

	$ git clone git://github.com/rjyo/twitter-2-weibo.git
	$ cd twitter-2-weibo
	$ git checkout single

下载编译用的和库支持管理的 Maven

	$ cd ..
	$ wget http://ftp.riken.jp/net/apache/maven/binaries/apache-maven-3.0.4-bin.tar.gz
  // 或者可在浏览器访问此列表获取: http://ftp.riken.jp/net/apache/maven/binaries/
	$ tar zxvf apache-maven-3.0.3-bin.tar.gz

编译	

	$ cd twitter-2-weibo	
	$ ../apache-maven-3.0.3/bin/mvn install:install-file -Dfile=./lib/cron4j-2.2.3.jar -DgroupId=cron4j -DartifactId=cron4j -Dversion=2.2.3 -Dpackaging=jar
	$ ../apache-maven-3.0.3/bin/mvn compile
	$ ../apache-maven-3.0.3/bin/mvn -Dmaven.test.skip=true package
	
看见如下内容就说明编译通过了
	
	[INFO] Reactor Summary:
	[INFO]
	[INFO] h2weibo ........................................... SUCCESS [0.001s]
	[INFO] core .............................................. SUCCESS [5.098s]
	[INFO] web ............................................... SUCCESS [55.546s]
	[INFO] worker ............................................ SUCCESS [0.926s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 1:01.692s
	[INFO] Finished at: Fri Oct 07 15:15:24 JST 2011
	[INFO] Final Memory: 14M/39M
	[INFO] ------------------------------------------------------------------------


## Step.3 云

接下来就是安装 vmc 和将应用推送至 cloudfoundry 的云服务了。如果提示没有 gem 这个命令，请参见[这里](http://rubygems.org/)。

	$ gem install vmc
	$ vmc target api.cloudfoundry.com

登录 cloudfoundry.com

	$ vmc login
	Email: jxxx.xxx@xxx.com
	Password: **********
	Successfully logged into [http://api.cloudfoundry.com]

将下面的 h2w-test1 改为你任意的应用名

	$ vmc push h2w-test1 --path ./web/target/h2weibo
	Application Deployed URL [" h2w-test1.cloudfoundry.com"]:
	Detected a Java Web Application, is this correct? [Yn]: y
	Memory Reservation ("64M", "128M", "256M", "512M", "1G") ["512M"]: 256M
	Creating Application: OK
	Would you like to bind any services to ' h2w-test1'? [yN]: y
	Would you like to use an existing provisioned service? [yN]: n
	The following system services are available
	1: mongodb
	2: mysql
	3: postgresql
	4: rabbitmq
	5: redis
	Please select one you wish to provision: 5
	Specify the name of the service ["redis-b529f"]:  h2w-test1
	Creating Service: OK
	Binding Service [ h2w-test1]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (6M): OK
	Push Status: OK
	Staging Application: OK
	Starting Application: .

## Step.4 使用

直接访问以刚才的应用名打头的地址 http://h2w-test1.cloudfoundry.com 即可

## 反馈信息

* via [@StXh](http://twitter.com/StXh) - 安装成功了。maven不能用ubuntu 11.10源里的，下载了安装文档里的才能编译过
* via [@cneeduy](http://twitter.com/cneeduy) - h2weibo搭建成功了，编译要用jdk1.6，否则过不了
* via [@lelige](http://twitter.com/lelige) - 自建 #H2Weibo 注意事项：在墙内执行“gem install vmc”会失败，建议VPN；JDK要预先装好，JRE是不够的
