Run/Debug
=========

* Open project in Intellij
* Open _edit configurations_ to create a new run/debug configuration
* Choose a new gradle configuration and name it `build and run` that runs `./gradlew runIde`
![Create debug configuration](website/debug_howto.png)
* Hit debug button as usual

Running from command line
-------------------------
* Execute command 
```shell script
  ./gradlew runIde
```

Override running IDE
--------------------

* Create `local.properties` file in root directory of the project with content:
```
# Uncomment when you want to use local IDE instead install specified IDE by ideOverride
#localIdePath=C:\\Users\\[USER]\\AppData\\Local\\Programs\\IntelliJ IDEA Ultimate

# Allowed types
# IU - Intellij Idea Ultimate
# IC - Intellij Idea Community
# IA - Android Studio
ideOverride=IU
```
* Select which IDE you want to use or uncomment `localIdePath` and specify the path to IDE.

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
* Implement its abstract methods
* Add new entry in `QuickListAction.kt` like this
```kotlin
addAction("com.developerphil.adbidea.action.NewAction", group)
```
