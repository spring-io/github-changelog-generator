#!/bin/bash
set -e

source $(dirname $0)/common.sh

git clone git-repo release-git-repo

pushd release-git-repo > /dev/null

snapshotVersion=$( get_revision_from_pom )
releaseVersion=$( strip_snapshot_suffix "$snapshotVersion" )
nextVersion=$( bump_version_number "$snapshotVersion" )

echo "Releasing $releaseVersion (next version will be $nextVersion)"
set_revision_to_pom "$releaseVersion"
git config user.name "Spring Buildmaster" > /dev/null
git config user.email "buildmaster@springframework.org" > /dev/null
git add pom.xml > /dev/null
git commit -m"Release v$releaseVersion" > /dev/null
git tag -a "v$releaseVersion" -m"Release v$releaseVersion" > /dev/null
build

echo "Setting next development version (v$nextVersion)"
git reset --hard HEAD^ > /dev/null
set_revision_to_pom "$nextVersion"
git add pom.xml > /dev/null
git commit -m"Next development version (v$nextVersion)" > /dev/null

popd > /dev/null

echo $releaseVersion > built-artifact/version
echo v$releaseVersion > built-artifact/tag
cp release-git-repo/target/github-changelog-generator.jar built-artifact/
java -jar release-git-repo/target/github-changelog-generator.jar --changelog.github.username=${GITHUB_USERNAME} --changelog.github.password=${GITHUB_TOKEN} --changelog.github.organization=spring-io --changelog.github.repository=github-changelog-generator $releaseVersion built-artifact/release-notes.md
