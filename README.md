# Twitter2Weibo

Welcome! This app syncs what you tweet from Twitter to Sina Weibo.

I'm hosting an instance on CloudFoundry.com. Access http://h2weibo.cloudfoundry.com/ and sync right now.

## Features:
  * Use OAuth to connect to Weibo and Twitter, no user/password saved by this app
  * Sync in less than 2 minutes
  * Various options to control the behavior
    * Drop tweets with @somebody style metions
    * Sync geolocation information
    * Auto upload photos to Weibo, with supports for yfrog, twitpic, instagram, camera+ and img.ly
    * Auto expand bit.ly URL
    * Auto translate twitter style #tag to weibo style #tag#
    * Auto rename user ID, for example, change @xuzhe in Twitter to @xu_zhe in Weibo

## Build

This project is using maven2 to build and managing its artifects. 2 web apps will be built, the web app and the background worker app.

Redis is used as the backend storage. Download and install it from http://redis.io

I use [IntelliJ IDEA Community Edition](http://www.jetbrains.com/idea/free_java_ide.html) for the development. It works like magic!

### Initialize

```bash
    $ brew install redis # for mac
    $ make setup # create maven 2 repo
```

### Compile & build WARs

```bash
    $ make
```

### Run

```bash
    $ make run # starts the local tomcat server
```

Access http://localhost:3000 for web app. To starts the worker, drop h2weibo-w1.war to some web container (like tomcat)

### Get Docs

```bash
    $ mvn dependency:resolve -Dclassifier=javadoc
    $ mvn dependency:resolve -Dclassifier=sources
```

### Upload to cloud foundry

```bash
    $ make update
```

## Contact:
  * Poke me, [@xu\_lele](http://twitter.com/xu_lele) on twitter
  * Comment me on [http://jyorr.com](http://jyorr.com)
  * Write an email to jyo.rakuraku on gmail.com


## License

(The MIT License)

Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.