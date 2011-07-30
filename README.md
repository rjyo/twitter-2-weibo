# Twitter2Weibo

Welcome! This app syncs what you tweet from Twitter to Sina Weibo.

I'm hosting an instance on CloudFoundry.com. Access http://h2weibo.cloudfoundry.com/ and sync right now.

## Features:
  * Use oauth to connect to Weibo, no need for user/password
  * Sync in less than 5 minutes
  * Auto drop tweets with @somebody style metions
  * Auto expand bit.ly URL
  * Auto translate twitter style #tag to weibo style #tag#

## Build

This project is using maven2 to build and managing its artifects. 2 web apps will be built, the web app and the background worker app.

Redis is used as the backend storage. Download and install it from http://redis.io

### Initialize

```bash
    $ brew install redis # for mac
    $ make setup # create maven 2 repo
```

### Compile & build WARs

    $ make

### Run

    $ make run # starts the local tomcat server

Access http://localhost:3000 for web app. To starts the worker, drop h2weibo-w1.war to some web container (like tomcat)

### Get Docs

    $ mvn dependency:resolve -Dclassifier=javadoc
    $ mvn dependency:resolve -Dclassifier=sources

### Upload to cloud foundry

    $ make update

## Contact:
  * Poke me, [@xu\_lele](http://twitter.com/xu_lele) on twitter
  * Comment me on [http://jyorr.com](http://jyorr.com)
  * Write an email to jyo.rakuraku on gmail.com
