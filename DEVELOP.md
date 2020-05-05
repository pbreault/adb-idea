Run/Debug
=========

* Open project in intellij
* Create gradle.properties file, use [instruction](gradle.properties.change_me)
* Open _edit configurations_ to create a new run/debug configuration
* Choose a new gradle configuration and name it `build and run` that runs `./gradlew buildPlugin runIde`
![Create debug configuration](website/debug_howto.png)
* hit debug button as usual

Running from command line
-------------------------
* Create gradle.properties file, use [instruction](gradle.properties.change_me)
* Execute command 
```shell script
  ./gradlew buildPlugin runIde
```

Create new menu item
====================

* Add entry to plugin.xml inside actions tab (below line 100)
```xml
            <action id="com.developerphil.adbidea.action.NewAction"
                    class="com.developerphil.adbidea.action.NewAction"
                    text="New Action"
                    description="Playing with the plugin">
            </action>
```

* Create and implement a new `NewAction` class that extends from `AdbAction` (you can create that from the plugin view, right click on the class name and choose `create class`
* implement its abstract methods
* add new entry in `QuickListAction.kt` like this
```kotlin
        addAction("com.developerphil.adbidea.action.NewAction", group)
```