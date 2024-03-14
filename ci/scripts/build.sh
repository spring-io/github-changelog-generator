#!/bin/bash
set -e

source $(dirname $0)/common.sh

pushd git-repo > /dev/null

./gradlew build

popd > /dev/null
