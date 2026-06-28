#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="${SCRIPT_DIR}/.."

OLD_VERSION="$1"
NEW_VERSION="$2"

shopt -s globstar
gsed -i "s/<version>${OLD_VERSION}<\\/version>/<version>${NEW_VERSION}<\\/version>/g" ${ROOT}/**/pom.xml
