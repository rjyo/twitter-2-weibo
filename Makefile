TARGET_DIR = ./target/h2weibo
MVN_INSTALL_FLAGS = install:install-file -Dfile=./lib/cron4j-2.2.3.jar -DgroupId=cron4j -DartifactId=cron4j -Dversion=2.2.3 -Dpackaging=jar

compile: clean
	@mvn package

setup:
	@mvn $(MVN_INSTALL_FLAGS)

clean:
	@mvn clean

update:
	@vmc update h2weibo --path $(TARGET_DIR)

logs:
	@vmc logs h2weibo

stats:
	@vmc stats h2weibo

dev: compile
	@cp $(TARGET_DIR).war /usr/local/Cellar/tomcat/7.0.6/libexec/webapps

restart:
	@vmc stop h2weibo
	@vmc start h2weibo

.PHONY: compile
