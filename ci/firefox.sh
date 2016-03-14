#! /bin/bash
echo "copying js files"
cp target/prod/public/js/app.js firefox/data/app.js
echo "copying css files"
cp resources/public/css/site.css firefox/data/site.css
cd firefox
jpm xpi
