TARGET_DIR = ./web/target/h2weibo
MVN_INSTALL_FLAGS = install:install-file -Dfile=./lib/cron4j-2.2.3.jar -DgroupId=cron4j -DartifactId=cron4j -Dversion=2.2.3 -Dpackaging=jar

compile: clean
	@mvn package

setup:
	@mvn $(MVN_INSTALL_FLAGS)

clean:
	@mvn clean

run:
	@mvn tomcat:run

update: compile
	@vmc update h2weibo --path $(TARGET_DIR)

logs:
	@vmc logs h2weibo

stats:
	@vmc stats h2weibo

restart:
	@vmc stop h2weibo
	@vmc start h2weibo

.PHONY: compile
