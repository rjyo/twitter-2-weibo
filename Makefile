TARGET_DIR = ./war
CLASSES = $(TARGET_DIR)/WEB-INF/classes
LIBS = $(TARGET_DIR)/target/h2weibo
MVN_INSTALL_FLAGS = install:install-file -Dfile=./lib/cron4j-2.2.3.jar -DgroupId=cron4j -DartifactId=cron4j -Dversion=2.2.3 -Dpackaging=jar

compile: clean
	@mvn package

clean:
	@mvn clean

update: compile
	@vmc update h2weibo --path $(TARGET_DIR)

logs:
	@vmc logs h2weibo

stats:
	@vmc stats h2weibo

setup:
	@mvn $(MVN_INSTALL_FLAGS)

.PHONY: compile
