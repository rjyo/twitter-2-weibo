GAE_JAVA_SDK_PATH = ../appengine-java-sdk-1.5.0.1
TARGET_DIR = war

package:
	@ant datanucleusenhance

dev_server:
	@ant runserver

update_app:
	@$(GAE_JAVA_SDK_PATH)/bin/appcfg.sh update $(TARGET_DIR)

.PHONY: package
