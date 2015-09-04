#!/bin/bash

set -ev

cd server/

# Specify what you need to run the server project test cases.
npm install

cd $TRAVIS_BUILD_DIR/
