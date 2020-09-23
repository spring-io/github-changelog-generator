#!/bin/bash
set -e

source $(dirname $0)/common.sh

pushd git-repo > /dev/null
version=$( get_revision_from_pom )
build
popd > /dev/null

cp git-repo/target/github-changelog-generator.jar built-artifact/
echo $version > built-artifact/version
