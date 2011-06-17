# Twitter2Weibo

Welcome! This app syncs what you tweet from Twitter to Sina Weibo.

## Features:
  * Use oauth to connect to Weibo, no need for user/password
  * Sync in less than 5 minutes
  * Auto drop tweets with @somebody style metions
  * Auto expand bit.ly URL
  * Auto translate twitter style #tag to weibo style #tag#

## Usage:
  1. Access http://h2weibo.cloudfoundry.com/auth?u=your\_twitter\_id in browser
  2. Boom!

## FAQ:

  *Q1:* What if I get error message from Sina API
  *A1:* Refresh your browser.

  *Q2:* Why my tweet like "I support 32 * 2 ..." is missing
  *A2:* Weibo made the decision.

## Contact:
  * Poke me, [@xu\_lele](http://twitter.com/xu_lele) on twitter
  * Comment me on [http://jyorr.com](http://jyorr.com)
  * Write an email to jyo.rakuraku on gmail.com

## Build

    $ make setup # for the first time
    $ make

### Get Docs

    $ mvn dependency:resolve -Dclassifier=javadoc
    $ mvn dependency:resolve -Dclassifier=sources

### Upload to cloud foundry

    $ make update

## TODO

1. Upload images directly to Weibo
