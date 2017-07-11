#!/usr/bin/env bash

BUILD_DIR=src/android_foo


build() {
	cd $BUILD_DIR
	./gradlew build 
	cd ../..
}

buildDebug() {
	cd $BUILD_DIR
	./gradlew build --debug
	cd ../..
}
clean() {
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

. src/android_foo/gradlew build
