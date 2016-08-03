# GitHub Omnibar

[![Chrome Web Store](https://img.shields.io/chrome-web-store/d/njccjmmakcbdpnlbodllfgiloenfpocb.svg?maxAge=2592000)]()
[![Github All Releases](https://img.shields.io/github/downloads/jcouyang/gh-omnibar/total.svg?maxAge=2592000)]()

Dear :octocat: GitHub:

BitBucket has an [awesome omnibar](https://developer.atlassian.com/blog/2016/02/6-secret-bitbucket-features/?categories=git#omnibar). I guess you also deserve one, so I made you an even better one.

You're welcome.

🍻

## Install

### Chrome
 👉 [webstore](https://chrome.google.com/webstore/detail/github-omnibar/njccjmmakcbdpnlbodllfgiloenfpocb?utm_source=chrome-ntp-icon)

 👉 [download](https://github.com/jcouyang/gh-omnibar/releases/download/v0.1.2/chrome.crx)

### Firefox
 👉 [download](https://github.com/jcouyang/gh-omnibar/releases/download/v0.1.2/github_omnibar-0.1.2-fx.xpi)

 Then drag it to Firefox.

## How to use
Press <kbd>p</kbd> and 🎉.

![](https://www.evernote.com/l/ABcsG--4RF9MgbcJanT6Vb9l_8LRfDILYMUB/image.png)

## Dev
It's written in ClojureScript with reagent and then compiled to JavaScript.

### Setup Locally

1. `lein figwhell`
2. open http://localhost:3449

### Build Chrome and Firefox extensions

See scripts in `ci` folder.
