#! /bin/bash
echo "copying js files"
cp target/prod/public/js/app.js chrome/js/app.js
cp resources/public/js/* chrome/js/
echo "copying css files"
cp resources/public/css/site.css chrome/css/site.css
