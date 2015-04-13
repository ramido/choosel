# Scope #

Choosel is built on top of [GWT](http://code.google.com/webtoolkit/) and the [Google App Engine](http://code.google.com/appengine/) (the backend can be modified to run on any servlet container). It is a **research prototype**. The current limitations are:

  * **Small number of data items** (up to several thousand rows with ~1000 visible items / visualization) - see [performance testing results](http://code.google.com/p/choosel/wiki/Performance). If larger data sets should be analyzed, the server part of Choosel can be extended to perform an initial automatic analysis or to sample the data.
  * **Exploration of static, read-only data**. Choosel assumes that the data does not change, and that it cannot be modified.
  * **Firefox 3.5+, Chrome 4+, and Safari 5+ are supported**. There are problems with the CSS rendering in Internet Explorer and older browser. Choosel also depends on fast JavaScript engines.
  * **Designed for mouse interaction and full screen windows on larger monitors (1280x1024)**. Touch interaction (e.g. iPad) is different in that hovering (and thus highlighting and tooltips) is difficult, and occlusion is problematic. Also, multitouch interaction is not supported. Similarly, small screen devices (e.g. smartphones) are unsuitable for Choosel.

For more details on the design decisions, see the [blog post on the design decisions (Why Choosel is based on GWT)](http://lgrammel.blogspot.com/2010/09/why-choosel-is-based-on-gwt.html).

# Architecture Overview #

![http://choosel.googlecode.com/svn/wiki/images/choosel-architecture.png](http://choosel.googlecode.com/svn/wiki/images/choosel-architecture.png)

The Choosel client is written in [GWT](http://code.google.com/webtoolkit/) and runs in the browser. The Choosel backend is written for the [Google App engine](http://code.google.com/appengine/), but it can be modified to run on any [Java Servlet container](http://www.oracle.com/technetwork/java/index-jsp-135475.html).

The client-side framework facilitates the interaction with **visualization components**, which can be wrappers around third party components and toolkits such as the Simile Timeline, Protovis and FlexViz. Choosel can integrate components developed using different technologies such as Flash and JavaScript.

Choosel provides several services such as view coordination and configuration, a desktop, undo/redo management, data management etc to make development of multi-view visualization environments easy.

# Visualization Component Architecture #

![http://choosel.googlecode.com/svn/wiki/images/choosel-visualization_component_architecture-thumbnail.png](http://choosel.googlecode.com/svn/wiki/images/choosel-visualization_component_architecture-thumbnail.png)
Visualization Component Architecture Overview ([enlarge diagram](VisualizationComponentArchitectureDiagram.md))

The Choosel visualization component architecture separates the core functionality (which is required to use Choosel visualizations in GWT) from the workbench functionality. The visualizations are extracted into separate visualization modules. The architecture consists of three main components:

  * **Core module**: The choosel.core module contains the core functionality that is required by Choosel visualizations. This includes the resource (i.e. data) framework, the management of visualization states (e.g. data, highlighting, selection), the visualization component API, and also more general services such as logging (which wraps gwt-log). The choosel.core module needs to be inherited by any GWT module that uses Choosel, whether it is a visualization component, a GWT application or a Choosel workbench.
  * **Visualization modules**: Visualization modules provide one or more visualization components that implement the Choosel visualization component API. They can wrap around other libraries, e.g. GWT modules, JavaScript visualization toolkits, or Flash widgets. Choosel provides several visualization modules (map, timeline, text, chart, and graph) that can be used right away.
  * **Workbench module**: The choosel.workbench module provides the persistence and sharing facilities as well as the visualization workspace.

This separation of concerns makes reusing and extending Choosel easier. It enables three ways to leverage Choosel in your own projects:

  * **Developing your own visualization components**: You can implement visualization components that adher to the Choosel visualization component API. These visualization components can then be used by yourself and others to take advantage of Choosel features such as management of view synchronization, management of selections, and support for hovering and details on demand.
  * **Using Choosel visualization components in your GWT application**: You can use one or several Choosel visualization components as widgets in your GWT application to visualize your data.
  * **Creating a Choosel-based workbench**: You can extend the whole Choosel framework to develop your own visualization workbench, for example for a specific application domain.

_Please note that while the modularization itself is complete, the visualization component API is still under development. We plan to release a first stable version in the next few months._

Related Blog Post: [Lars' Notes: Choosel Visualization Component Architecture](http://lgrammel.blogspot.com/2011/02/choosel-visualization-component.html)

Related Wiki Page: [Choosel GWT Modules](Modules.md)

# User Interface Elements #

![http://choosel.googlecode.com/svn/wiki/images/choosel-ui-elements.png](http://choosel.googlecode.com/svn/wiki/images/choosel-ui-elements.png)

  * [ActionBar source code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/ui/ActionBar.java)
  * [Desktop Interface source code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/windows/Desktop.java)
  * [Desktop Implementation source code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/windows/DefaultDesktop.java)
  * [Window source code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/windows/WindowPanel.java)

![http://choosel.googlecode.com/svn/wiki/images/choosel-ui-window.png](http://choosel.googlecode.com/svn/wiki/images/choosel-ui-window.png)

  * [Window source code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/windows/WindowPanel.java)
  * [View interface](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/views/View.java)
  * [Default View implementation](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/views/DefaultView.java)
  * [View Content Display interface](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/views/ViewContentDisplay.java)

# Data Representation #

The data is stored in [Resource](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/resources/Resource.java)s, which are essentially key-value maps with an id. [Resource Sets](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.core/src/org/thechiselgroup/choosel/core/client/resources/ResourceSet.java) combine several resources. There are multiple implementations of ResourceSet, which are required for divserse purposes such as memory management.