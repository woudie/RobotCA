#!/usr/bin/env bash

BUILD_DIR=src/android_foo
APK_DIR=control_app/build/outputs/apk
APK_NAME=control_app-debug.apk

build() {
	cd $BUILD_DIR
	./gradlew build -x lint
	if [ -e  $APK_DIR/$APK_NAME ]; then
		read -t 3 -p "Install app?, 3 sec timeout (y/n)";
		if [ "$REPLY" == "y" ]; then
			cd $APK_DIR
			adb install -r $APK_NAME
			echo "App installed"
			adb shell am start -n com.robotca.ControlApp/com.robotca.ControlApp.SplashActivity
			cd ../../..
		fi;
	else
		echo "Build failed!!!!"
	fi;
	cd ../..
}

buildDebug() {
	cd $BUILD_DIR
	./gradlew build --debug
	cd ../..
}
clean() {
	rm -rf src/android_foo/build
	rm -rf src/android_foo/control_app/build
	rm -rf src/android_foo/control_app_lib/build
	cd $BUILD_DIR
	./gradlew clean
	cd ../..
}

anythingElse () {
	echo " "
	echo " "
	echo "Anything else?"
	select more in "Yes" "No"; do
		case $more in
			Yes ) bash build.sh; break;;
			No ) exit 0; break;;
		esac
	done ;
}

echo " "
echo " "
echo "Please select one of the following options:"
echo " "
select build in "Build" "Build with debug" "Clean" "Exit"; do
	case $build in
		"Build" ) build; anythingElse; break;;
		"Build with debug" ) buildDebug; anythingElse; break;;
		"Clean" ) clean; anythingElse; break;;
		"Exit" ) exit 0; break;;
	esac
done
exit 0
