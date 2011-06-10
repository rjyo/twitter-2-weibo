GAE_JAVA_SDK_PATH = ../appengine-java-sdk-1.5.0.1
TARGET_DIR = war
LOCAL_DB = ./war/WEB-INF/appengine-generated/local_db.bin

compile:
	@ant datanucleusenhance

dev_server:
	@ant runserver

clear_data:
	@if [ -f $(LOCAL_DB) ]; then rm $(LOCAL_DB); fi;

update_app:
	@ant update

.PHONY: compile
