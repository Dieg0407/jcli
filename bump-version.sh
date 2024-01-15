#!/bin/sh

LAST_COMMIT_MSG=$(git log -1 --pretty=%B)

CURRENT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
NEXT_VERSION=$(semver next --commit-message "$LAST_COMMIT_MSG" --last-version "$CURRENT_VERSION")

./mvnw versions:set -DnewVersion="$NEXT_VERSION" -DgenerateBackupPoms=false
git add pom.xml