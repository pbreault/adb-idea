Run/Debug
=========

* Open project in intellij
* Open _edit configurations_ to create a new run/debug configuration
* Choose a new gradle configuration and name it `build and run` that runs <code>./gradlew buildPlugin runIdea</code>
![Create debug configuration](website/debug_howto.png)
* hit debug button as usual

Running from command line
-------------------------
<code>
        ./gradlew buildPlugin runIdea
</code>

Create new menu item
====================

* Add entry to plugin.xml (below line 100)
<code>

            <action id="com.developerphil.adbidea.action.NewAction"
                    class="com.developerphil.adbidea.action.NewAction"
                    text="New Action"
                    description="Playing with the plugin">
            </action>
</code>

* Create and implement a new `NewAction` class that extends from `AdbAction` (you can create that from the plugin view, right click on the class name and choose `create class`
* implement its abstract methods
* add new entry in `QuickListAction.java` like this
<code>

        addAction("com.developerphil.adbidea.action.NewAction", group);
</code>