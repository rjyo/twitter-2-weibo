GAE_JAVA_SDK_PATH = ~/Downloads/appengine-java-sdk-1.5.0.1

package:
	@mvn clean package

dev_server:
	@$(GAE_JAVA_SDK_PATH)/bin/dev_appserver.sh target/hello-weibo-1.0/

update_app:
	@$(GAE_JAVA_SDK_PATH)/bin/appcfg.sh update target/hello-weibo-1.0/

.PHONY: package
