# Setting up Java, GAE/J & GWT #

  * Install [Java](http://www.java.com/en/download/manual.jsp) (at least Java 6 is required because we @Override annotations for interface implementations. If you manually get rid of @Override annotations, Java 5 should work, too)
  * Install [Google Web Toolkit (GWT)](http://code.google.com/webtoolkit/download.html) (Choosel requires [GWT 2.1](http://code.google.com/webtoolkit/versions.html) (GWT 2.2 does not work right now)) - please download GWT 2.1 from [here](http://code.google.com/webtoolkit/versions.html)
  * Install [Google App Engine SDK for Java](http://code.google.com/appengine/downloads.html) (Version 1.4 or later is recommended)

# Setting up Eclipse #

We recommend using [Eclipse](http://www.eclipse.org) for working on Choosel. The following setup is known to work:

  * [Eclipse IDE for Java Developers 3.6 (Helios)](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/heliosr)
    * Make sure that the version of Eclipse works with the Java version (e.g. 64bit Eclipse with 64bit Java, or 32bit/32bit)
  * Subversion Plugin for Eclipse (you can use Help-->Eclipse Marketplace to install it)
    * [Subversive](http://www.eclipse.org/subversive/downloads.php)
    * [Team Providers](http://www.polarion.com/products/svn/subversive/download.php)
    * Change Setting in Eclipse Preferences: Window/Preferences-->Team/SVN-->SVN Connector-->SVN Connector = SVNKit 1.3.0
  * [Google Plugin for Eclipse](http://code.google.com/eclipse/)

# Checking out Choosel and the example workbench from the SVN repository #

  * [General project checkout information](http://code.google.com/p/choosel/source/checkout)
  * Check out "org.thechiselgroup.choosel.core" as Eclipse project
  * Check out "org.thechiselgroup.choosel.dnd" as Eclipse project
  * Check out "org.thechiselgroup.choosel.protovis" as Eclipse project
  * Check out "org.thechiselgroup.choosel.visualization\_component.chart" as Eclipse project
  * Check out "org.thechiselgroup.choosel.visualization\_component.graph" as Eclipse project
  * Check out "org.thechiselgroup.choosel.visualization\_component.map" as Eclipse project
  * Check out "org.thechiselgroup.choosel.visualization\_component.text" as Eclipse project
  * Check out "org.thechiselgroup.choosel.visualization\_component.timeline" as Eclipse project
  * Check out "org.thechiselgroup.choosel.workbench" as Eclipse project
  * Check out "org.thechiselgroup.choosel.example.workbench" as Eclipse project

# Run Choosel #

You should be able to run "choosel.example.workbench" in your development development environment now - just run the "choosel.example.workbench" launch configuration.

If you encounter problems setting up choosel with this guide, complain on the [choosel mailing list](http://groups.google.com/group/choosel) :-)

**IMPORTANT**: Please be aware that Choosel is under active development and has not been released yet. Your code might break when updating Choosel due to changes that we make, and it is thus important to follow the [commits](http://code.google.com/p/choosel/updates/list) that we make. If you're code break during a Choosel update and you can't figure out why, complain on the [choosel mailing list](http://groups.google.com/group/choosel) :-)

**More information and news on Choosel can be found at**<a href='http://lgrammel.blogspot.com/search/label/choosel'>lgrammel.blogspot.com</a>**.**

# Optional setup #

  * To stay on top of code commits to Choosel, please consider installing the [Eclipse RSS Plugin](http://www.junginger.biz/eclipse/rss-view.html) and configure it with the [subversion commit feed for Choosel](http://code.google.com/feeds/p/choosel/svnchanges/basic). You might also want to add the feed for [issue tracker updates](http://code.google.com/feeds/p/choosel/issueupdates/basic).
  * If you work on choosel issue tracker items, you might want to consider using the [Mylin-Google Code bridge](http://code.google.com/p/googlecode-mylyn-connector/) to get issue tracker entries into Eclipse/Mylin.  You can follow instructions 2 to 6 on [Alex Ruiz's Weblog](http://www.jroller.com/alexRuiz/entry/using_mylyn_with_google_code/) to set up issue tracking with Google Code.
  * If you use Eclipse 3.6 abbreviated package names (Preferences-->Java/Appearance), the following abbreviations might be useful for you:
```
org.thechiselgroup.choosel=[choosel]
```