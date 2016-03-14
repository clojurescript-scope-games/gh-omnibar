#! /bin/bash
lein cljsbuild once prod
./ci/chrome.sh
./ci/firefox.sh
