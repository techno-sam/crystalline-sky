#!/bin/bash

# Creates a tagged version. Releases must be manually created.

if [ $# -ne 1 ]; then
  echo "Usage: $0 \"<release message>\""
  exit 1
fi

raw_version=$(grep -E 'modVersion(\s*)=(\s*)' "./gradle.properties" | cut -d'=' -f2 | tr -d ' ')
branch_name=$(grep -E 'branchName(\s*)=(\s*)' "./gradle.properties" | cut -d'=' -f2 | tr -d ' ')

version="$raw_version+$branch_name"

echo "Creating tag for version $version with message '$1'"

git tag -s -a v"$version" -m "$1"

git push origin v"$version"
