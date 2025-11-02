#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="${SCRIPT_DIR}/.."

OLD_VERSION="$1"
NEW_VERSION="$2"

shopt -s globstar
gsed -i "s/${OLD_VERSION}/${NEW_VERSION}/g" ${ROOT}/**/pom.xml
