# edblog #

So I woke up one morning with a burning desire to inflict upon the humble blog a paradigm shift that would encapsu--

Wait, let me try again.

So I woke up one morning having been divinely inspired to reinvent the humble blog--

Okay, no, one more try.

So I was messing around with Docker and with clusterization solutions like [Mesos](http://mesosphere.io) and decided that I was going to do everything I could to avoid saddling myself with a LAMP stack for any reason, and that included replacing Wordpress. *edblog* is that replacement. It's probably more defined in terms of what it's **not**: it's a very simple dynamic server for Markdown'd blog posts. And that's all. There's no module system, no extension points (beyond the barest minimum needed for theming the application). There's also no admin panel. Database to cache to your web browser and done.

It's the minimum viable blog for my needs. Couldn't be happier.

## Installation ##

Installation is pretty straightforward. In keeping with the "batteries not included" nature of this (I mean, it exists as a project for me first and foremost, if anyone else likes it, groovy), you'll need to do a little work for setup.

```bash
git clone https://github.com:eropple/edblog.git
cd edblog
cp conf/application.conf{.sample,}
vim conf/application.conf
JAVA_OPTS="-Dedblog.run_ddl=Y" play start
play start
```

I have some Docker scripts lying around that I'll eventually make public (if I remember, and if anyone cares).

## Usage ##

It certainly isn't complex, I'll tell you that. Posts are posted by inserting into the database. So are pages. At some point I may add a backend, but for now--meh. :-)


## Future Stuff? ##

I have no idea. It basically meets my needs. Poking things into database rows doesn't bother me. There are the stubs of a comment system in here, but for now, Disqus commenting suffices for me.

If you'd like to extend it for your own use cases, feel free to get in touch and we can figure out a way to collaborate. This is open source mostly in case it helps folks, it's not really a Big Project for me, but contributions are always cool.
