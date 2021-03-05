#!/bin/bash
set -Eeuo pipefail
cd "$(dirname "$(readlink -f "$0")")"

base_repo_dir="$(pwd)"
tmp_dir="$(pwd)/.tmp_docs"
docs_dir="$(pwd)/.tmp_docs/docs"

function generate() {
  git_rev="$(git rev-parse HEAD)"

  rm -rf "$docs_dir"
  mkdir -p "$docs_dir"

  # Copy swagger-ui
  pushd "$tmp_dir"
  npm i swagger-ui-dist@3.44.1
  rsync -av --progress 'node_modules/swagger-ui-dist/' "$docs_dir" \
    --exclude 'package.json' \
    --exclude 'README.md' \
    --exclude 'index.js' \
    --exclude 'swagger-ui.js' \
    --exclude 'absolute-path.js' \
    --exclude '*-es-*' \
    --exclude '*.map'
  swagger_urls=''
  swagger_urls+="{name: 'Users API', url: 'openapi-users.json?${git_rev}'},"
  swagger_urls+="{name: 'GPS API', url: 'openapi-gps.json?${git_rev}'},"
  swagger_urls+="{name: 'Rewards API', url: 'openapi-rewards.json?${git_rev}'},"
  sed -i 's#<title>Swagger UI</title>#<title>TourGuide API Documentation</title>#' "${docs_dir}/index.html"
  sed -i 's#url: "https://petstore.swagger.io/v2/swagger.json",#urls: ['"$swagger_urls"'], readOnly: true,#' "${docs_dir}/index.html"
  popd

  # Copy openapi specification
  ./gradlew generateOpenApiDocs
  function openapi_filter() {
    jq '.info.description = "<a href=\"https://github.com/np111/P8_tourguide\">View Source on GitHub</a>" | .servers = []'
  }
  openapi_filter <'gps/service/build/openapi.json' >"${docs_dir}/openapi-gps.json"
  openapi_filter <'rewards/service/build/openapi.json' >"${docs_dir}/openapi-rewards.json"
  openapi_filter <'users/service/build/openapi.json' >"${docs_dir}/openapi-users.json"
}

function publish() {
  pushd "$docs_dir"
  rm -rf .git
  git init
  cp "${base_repo_dir}/.git/config" '.git/config'
  git checkout --orphan docs
  git add .
  git commit -m 'Publish docs'
  git push -f origin docs
  popd
}

function main() {
  case "${1:-}" in
  generate)
    generate "$@"
    ;;
  publish)
    publish "$@"
    ;;
  *)
    echo "Usage: $0 generate|publish" >&2
    exit 1
    ;;
  esac
}

main "$@"
exit 0
