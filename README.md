# CDML Loader

CDML Loader is a collection of helper classes and annotations to help you organise your CDM application.
It separates the layout creation and controller code from each other by using a dedicated file for
the layout definition, written in a markup language called CDML, an XML document validated against the CDML DTD.

Requires CDM version `0.2.0-pre3`.

## Setup for application developers

### Step 1 - Libraries
The first step is to include the CDM jar in your Forge project.
Make sure it is scoped to `provided`.

Next, include the CDMLLoader jar in your Forge project.
Make sure it is scoped to `provided` too.

If you need any help concerning the Device Mod, check out [this repo](https://github.com/MrCrayfish/DeviceAPITutorial) from MrCrayfish.
It contains a demo application which is very useful to get started.

### Step 2 - Application Class

Create a new class that extends `com.mrcrayfish.device.api.app.Application`.
Make sure you use the appropriate registering calls to register your application in CDM during initialisation.

Add the annotation `@CdmlApp` at the top of the class definition.

Inside the `init()` method, call `CDMLLoader.load(this)`.

Your code should look something like this:
```java
import com.mrcrayfish.device.api.app.Application;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;

@CdmlApp
class MyApplication extends Application {
    //...
    
    @Override
    public void init() {
        CDMLLoader.load(this);
    }
    
    //...
}
``` 

### Step 3 - Designing the Application in CDML
Create a new folder in the `resources` folder of your mod called `cdml`. The structure will look something like this:
```
assets/
    mymod/
        cdml/
            ...
        lang/
            ...
        testures/
            ...
        ...
``` 
Let's suppose the ID of your application is `myapp` and your mod id is `mymod`.
Then you will need to create the a file called `myapp.cdml` in the folder `assets/mymod/cdml/`.

Every CDML document begins with this basic layout:
```xml
<?xml version="1.0" encoding="utf-8"?>
<application main="mainLayout">
    <layouts>
        <layout id="mainLayout" title="Main Layout" width="200" height="100">
            <components>
                ...
            </components>
        </layout>
        ...
    </layouts>
</application>
```

_To be continued._

### Step 4 - Writing the Controller
_To do._

## Setup for component developers
_To do._

## To Do List
- Add Component Handlers for...
    - ComboBox.Custom
    - (Upcoming colour picker)
- Implement...
    - Listeners (nearly done)
    - Renderers
    - Items for ComboBox and ItemList
    - Radio groups
    - Color schemes
    - More advanced structures (e.g. for ComboBox and ItemList)
- Sort out DTD
- UI Designer

## Thanks / Credits

* to [MrCrayfish](https://github.com/MrCrayfish) for CDM ([MrCrayfishDeviceMod](https://github.com/MrCrayfish/MrCrayfishDeviceMod)).
* to Java for reflection.