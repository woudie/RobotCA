#!/usr/bin/env bash

BUILD_DIR=src/android_foo


build() {
	cd $BUILD_DIR
	./gradlew build
	cd ../..
}

clean() {
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
select build in "Build" "Clean" "Exit"; do
	case $build in
		"Build" ) build; anythingElse; break;;
		"Clean" ) clean; anythingElse; break;;
		"Exit" ) exit 0; break;;
	esac
done
exit 0

. src/android_foo/gradlew build
