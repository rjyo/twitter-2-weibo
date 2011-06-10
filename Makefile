TARGET_DIR = ./war
CLASSES = $(TARGET_DIR)/WEB-INF/classes
LIBS = $(TARGET_DIR)/WEB-INF/lib

compile: clean
	@ant compile

clean:
	@if [ -d $(CLASSES) ]; then rm -rf $(CLASSES)/*; fi;

update: compile
	@vmc update h2weibo --path $(TARGET_DIR)

logs:
	@vmc logs h2weibo

stats:
	@vmc stats h2weibo

setup:
    @curl "https://github.com/downloads/xetorthio/jedis/jedis-2.0.0.jar" > $(LIBS)/jedis-2.0.0.jar

.PHONY: compile
