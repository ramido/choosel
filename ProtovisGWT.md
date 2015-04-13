Protovis-GWT ([Source Code](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.protovis/), [Examples](http://web.uvic.ca/~lgrammel/blog/protovis-gwt/index.html)) is an **open source GWT data visualization module**. It wraps the [Protovis](http://vis.stanford.edu/protovis/) JavaScript visualization API for usage in GWT. The goal of Protovis-GWT is to make the Protovis API available in Java for GWT.

The current version Protovis-GWT 0.4.1 ([Download GWT Module (jar)](http://choosel.googlecode.com/files/ProtovisGWT-0.4.1.jar), [Download Project (zip)](http://choosel.googlecode.com/files/ProtovisGWT-0.4.1-project.zip), [Source Code](http://code.google.com/p/choosel/source/browse/#svn%2Ftags%2Fprotovis-gwt%2F0.4.1%2Forg.thechiselgroup.choosel.protovis)) is an **early development version** based on Protovis 3.2 and GWT 2.3. Several [examples](http://web.uvic.ca/~lgrammel/blog/protovis-gwt/index.html) from the [Protovis example gallery](http://vis.stanford.edu/protovis/ex/) have been [re-implemented using Protovis/GWT](http://web.uvic.ca/~lgrammel/blog/protovis-gwt/index.html).

**The Protovis API has not been fully implemented in Protovis-GWT 0.4.1**. Version 0.4.1 implements the Protovis API for the basic Mark types (Mark, Area, Bar, Dot, Label, Line, Wedge) as well as the functionality for most conventional, custom, hierarchy and network examples from the Protovis website. The support for map visualizations as well as for advanced interactions is still limited. Protovis-GWT currently supports **Chrome**, **Firefox**, **Safari** and **IE 9**. IE 8 and older are not supported (see [Protovis Issue 15](http://code.google.com/p/protovis-js/issues/detail?id=15) for more information on Protovis support for IE).

## Getting Started ##

Setting up Protovis/GWT in your GWT project is easy:

  1. [Download the Protovis-GWT module jar file](http://choosel.googlecode.com/files/ProtovisGWT-0.4.1.jar)
  1. Add the jar file to the build path of your GWT project
  1. Inherit `org.thechiselgroup.choosel.protovis.ProtovisGWT` by adding `<inherits name='org.thechiselgroup.choosel.protovis.ProtovisGWT'/>` to your GWT module XML definition (`.gwt.xml`).
  1. Use the Protovis Widget in your code, e.g.
```
public void onModuleLoad() {
  RootPanel.get().add(new ProtovisWidget() {
    protected void onAttach() {
      super.onAttach();
      initPVPanel();
      // create visualization here...
      getPVPanel().render();
    }
  });
}
```

## Protovis Modifications ##
The Protovis JavaScript code was patched to allow for `instanceof Array` and `instanceof Date` tests across frames. This was necessary because the GWT scripts are loaded in a iframe, and the JavaScript script files are loaded in the main page.

## Callback Methods ##
Javascript supports functions with varying arguments of varying types. This feature is heavily used in Protovis. However, Java (and therefore GWT) does not support flexible functions to the same extent. To address this problem, Protovis-GWT instead uses function interfaces, e.g. [JsDoubleFunction](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.protovis/src/org/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction.java), with a generic [JsArgs](http://code.google.com/p/choosel/source/browse/trunk/org.thechiselgroup.choosel.protovis/src/org/thechiselgroup/choosel/protovis/client/jsutil/JsArgs.java) argument.

Callback methods can defined by implementing these function interfaces in anonymous inner classes. The inner classes can access final variables from the surrounding code, can access `this` used in Protovis via `args.getThis()`, and can access the arguments via `args.getObject`, `args.getDouble` etc.:
```
final PVLinearScale x = ... // final variables can be accessed
// ...
.width(new JsDoubleFunction() {
    public double f(JsArgs args) {
        PVMark _this = args.getThis();
        return x.fd(_this.index());
        // Protovis: y(this.index)
    }
})
// ...
.width(new JsDoubleFunction() {
    public double f(JsArgs args) {
        double d = args.getDouble(); 
        return x.fd(d);
        // Protovis: x(d)
    }
});
// ...
.visible(new JsBooleanFunction() {
    public boolean f(JsArgs args) {
        MyType d = args.getObject(1);
        // ...
    }
});
```
Java generics enable assigning the return values of `args.getThis()` and `args.getObject` to the expected types. The `args.getObject`-methods (which are available for the different primitive types as well) optionally accept an int parameter that specifies the argument index, e.g. `1` for the second argument. If the argument index is omitted, the argument at index `0` is returned.

Using anonymous inner classes adds boilerplate code that leads to lower readability compared to the original Protovis code. One way to address this problem is by supporting the definition of anonymous property functions using String literals as in [Protovis-Java](http://vis.stanford.edu/files/2010-Protovis-InfoVis.pdf). However, regular Java code has several advantages compared to String literals. Modern IDEs provide content assist, syntax highlighting, refactoring and error checking. The inner classes can also be extracted and unit-tested, if desired. For these reasons, Protovis-GWT uses anonymous inner classes and does not support the definition of anonymous property functions using String literals.

## Change Log ##
**0.4.1**: pan & zoom support (Protovis behaviors); ellipse mark support (Protovis-GWT specific extension); GWT 2.3 / Internet Explorer 9 support

**0.4**: support for network visualizations (Matrix, Force-directed, Arc); inlined classes into PV to align syntax closer to original Protovis syntax; added stable sort implementation to JsArrayGeneric; various API improvements

**0.3**: complete API implementation for basic Protovis Mark classes (Mark, Area, Bar, Dot, Label, Line, Wedge)

**0.2**: improved event handler interface; support for tree visualization and bubble chart examples

**0.1**: initial release

## Contributors ##
**Lars Grammel**: Main developer

**Bradley Blashko**: Initial prototype implementation

**Nikita Zhiltsov**: Force-directed Layout support

**Guillaume Godin**: Ellipse Example