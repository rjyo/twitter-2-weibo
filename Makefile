TARGET_DIR = war
CLASSES = ./war/WEB-INF/classes

compile: clean
	@ant compile

clean:
	@if [ -d $(CLASSES) ]; then rm -rf $(CLASSES)/*; fi;

update: compile
	@vmc update h2weibo --path ./war

logs:
	@vmc logs h2weibo

stats:
	@vmc stats h2weibo

.PHONY: compile
