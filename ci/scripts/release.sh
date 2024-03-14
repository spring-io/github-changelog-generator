#!/bin/bash
set -e

source $(dirname $0)/common.sh

git clone git-repo release-git-repo

pushd release-git-repo > /dev/null

snapshotVersion=$( awk -F '=' '$1 == "version" { print $2 }' gradle.properties )
releaseVersion=$( get_next_release "$snapshotVersion" )
nextVersion=$( bump_version_number "$snapshotVersion" )

echo "Releasing $releaseVersion (next version will be $nextVersion)"
sed -i "s/version=$snapshotVersion/version=$stageVersion/" gradle.properties
git config user.name "Spring Builds" > /dev/null
git config user.email "spring-builds@users.noreply.github.com" > /dev/null
git add gradle.properties > /dev/null
git commit -m "Release v$releaseVersion" > /dev/null
git tag -a "v$releaseVersion" -m"Release v$releaseVersion" > /dev/null

./gradlew build

echo "Setting next development version (v$nextVersion)"
git reset --hard HEAD^ > /dev/null
echo "Setting next development version (v$nextVersion)"
sed -i "s/version=$snapshotVersion/version=$nextVersion/" gradle.properties
git add gradle.properties > /dev/null
git commit -m "Next development version (v$nextVersion)" > /dev/null

popd > /dev/null

echo $releaseVersion > built-artifact/version
echo v$releaseVersion > built-artifact/tag
cp release-git-repo/build/libs/github-changelog-generator.jar built-artifact/
java -jar release-git-repo/build/libs/github-changelog-generator.jar --changelog.repository=spring-io/github-changelog-generator $releaseVersion built-artifact/changelog.md
