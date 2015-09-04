#!/bin/bash

set -ev

# Check whether 
if [ -d "$ANDROID_DIR/$ANDROID_PROJECT_NAME" ]; then

	cd $ANDROID_DIR/$ANDROID_PROJECT_NAME/
	chmod ugo+x ./gradlew

	# Run unit tests of Android Apps
	./gradlew test --continue

	cd $TRAVIS_BUILD_DIR/

fi
