# Github Omnibar

Dear :octocat: Github:

bitbucket has a [awesome omnibar](https://developer.atlassian.com/blog/2016/02/6-secret-bitbucket-features/?categories=git#omnibar). I guess you also deserve one, so I made you a even better one.

You're welcome.

🍻

## Install

### Chrome
 👉 [webstore](https://chrome.google.com/webstore/detail/github-omnibar/njccjmmakcbdpnlbodllfgiloenfpocb?utm_source=chrome-ntp-icon)

 👉 [download](https://github.com/jcouyang/gh-omnibar/releases/download/v0.1.0/chrome.crx)

### Firefox
 👉 [download](https://github.com/jcouyang/gh-omnibar/releases/download/v0.1.0/github_omnibar-0.1.0-fx.xpi)

 then drag it to Firefox

## How to use
press <kbd>p</kbd> and 🎉

![](https://www.evernote.com/l/ABcsG--4RF9MgbcJanT6Vb9l_8LRfDILYMUB/image.png)

## Dev
it's written in ClojureScript with reagent and then compile to JavaScript.

### Setup Locally

1. `lein figwhell`
2. open http://localhost:3449

### Build chrome and firefox extensions

see scripts in `ci` folder
