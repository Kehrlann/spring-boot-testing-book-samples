#!/usr/bin/env bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="${SCRIPT_DIR}/.."

echo "Finding Maven projects..."

find "${ROOT}" -name "mvnw" -type f | while read -r mvnw_path; do
    project_dir="$(dirname "${mvnw_path}")"

    # Verify pom.xml exists alongside mvnw
    if [[ -f "${project_dir}/pom.xml" ]]; then
        echo ""
        echo "=========================================="
        echo "Running tests in: ${project_dir}"
        echo "=========================================="

        cd "${project_dir}"
        ./mvnw spring-javaformat:validate test
    fi
done

echo ""
echo "All tests completed."
