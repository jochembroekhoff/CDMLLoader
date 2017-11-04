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
_To do._

### Step 4 - Writing the Controller
_To do._

## Setup for component developers
_To do._

## To Do List
- Add Component Handlers for...
    - ButtonArrow
    - ButtonToggle
    - ComboBox
    - ItemList
    - (Upcoming colour picker)
- Implement...
    - Listeners
    - More advanced structures (e.g. for ComboBox and ItemList)
- Sort out DTD
- UI Designer

## Thanks / Credits

* to [MrCrayfish](https://github.com/MrCrayfish) for CDM ([MrCrayfishDeviceMod](https://github.com/MrCrayfish/MrCrayfishDeviceMod)).
* to Java for reflection.