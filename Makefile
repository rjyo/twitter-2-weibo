MVN_INSTALL_FLAGS = install:install-file -Dfile=./lib/cron4j-2.2.3.jar -DgroupId=cron4j -DartifactId=cron4j -Dversion=2.2.3 -Dpackaging=jar

WEB_TARGET_DIR = ./web/target/h2weibo
WEB_NAME = h2w-single

compile: clean
	@mvn -Dmaven.test.skip=true package

setup:
	@mvn $(MVN_INSTALL_FLAGS)

clean:
	@mvn clean
	@find . -name "*.log" |xargs rm

run:
	@mvn tomcat:run

update: compile
	@vmc update ${WEB_NAME} --path $(WEB_TARGET_DIR)

logs:
	@vmc logs ${WEB_NAME}

stats:
	@echo stats for ${WEB_NAME}
	@vmc stats ${WEB_NAME}

restart:
	@vmc stop ${WEB_NAME}
	@vmc start ${WEB_NAME}

.PHONY: compile
