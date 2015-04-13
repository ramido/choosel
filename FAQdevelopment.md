## Compiling Choosel to JavaScript ##

  * **I compiled Choosel, but I get the message "GWT module org.thechiselgroup.choosel.example.workbench may need to be recompiled" when I access the compiled version locally ( 'http://127.0.0.1:8888/index.html' )**
> Make sure that you selected 'org.thechiselgroup.choosel.example.workbench', not 'org.thechiselgroup.choosel.workbench' or 'org.thechiselgroup.choosel.core' in the GWT compile dialog.

  * **Compiling org.thechiselgroup.choosel.example.workbench fails without any explanation**
> This might be an out of memory issue. Change the VM arguments in the GWT compile dialog under advanced to
```
   -Xmx768m
   -Xss20m
```

  * **Eclipse 3.4 - JUnit tests not working**
> Eclipse 3.4 ships with JUnit 4.5 - we use some newer features in our tests. You could either add JUnit 4.8.2 to the project, or update your Eclipse version (3.6 is recommended).