# CDML Loader

CDML Loader is a collection of helper classes and annotations to help you organise your CDM application.
It separates the layout creation and controller code from each other by using a dedicated file for
the layout definition, written in a markup language called CDML, an XML document validated against the CDML DTD.

Requires CDM version `0.4.0` or later, compatibility with other versions is not guaranteed.

*This documentation is NOT complete and you shouldn't rely on it. This project has not been released as
any stable release, beta or alpha, so anything might change. The demo application in the source code is a great reference
when you can't find how to do something in the documentation below. (src/main/java/nl/jochembroekhoff/cdmlloaderdemo/)*  

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

Inside the `init()` method, call `CDMLLoader.init(this)`.

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
        CDMLLoader.init(this);
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
        textures/
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
    <notifications> <!-- Notifications are not required -->
        ...
    </notifications>
</application>
```

You can have multiple layouts in the application. Each layout is used to display different information to the user.

At this moment, the following components are supported out of the box:
- Button
- ButtonToggle
- CheckBox
- ComboBox.List
- Image
- ItemList
- Label
- NumberSelector
- ProgressBar
- Slider
- Spinner
- Text
- TextArea
- TextField

And the following layouts:
- Layout (default)
- ScrollableLayout

To find out which attributes you can set on each component, checkout the DTD (in src/main/resources/assets/cdmlloader).

> **Using i18n**: All the core components support i18n (Internationalization) on all text attributes.
> You can mark a text value as i18n by prefixing it with a colon (:).
> ```xml
> <Label left="5" top="5" text=":labelText"/>
> ``` 
> The key in the lang files is composed like this: `app.MOD_ID.APP_ID.value.RAWVALUE`.
> So in this case CDMLLoader tries to load the i18n value of `app.mymod.myapp.value.labelText`.
>
> Note: Notifications are translated on the client which opens the application, this means that when you send a
> notification to another player whose client is set to a different language, the text is displayed in the language of
> the client that instantiated the notification.

_To be continued._

### Step 4 - Writing the Controller
If you want to use all the features of the Device Mod and support user interaction, you need a controller.
The controller class is simply the application class.

To interact with any component in the application, you need to tell CDMLLoader to link a generated component
to a field in your controller. This is done by adding the `id` attribute to the component in the XML document.

Your .cdml and controller would look something like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<application main="mainLayout">
    <layouts>
        <layout id="mainLayout" title="Main Layout" width="200" height="100">
            <components>
                <Label id="myLabel" top="5" left="5" text="Example Text"/>
                ...
            </components>
        </layout>
        ...
    </layouts>
    <notifications>
        <notification name="testNotify" title="Henlo!" subTitle="Just testing." iconName="EMAIL"/>
    </notifications>
</application>
``` 
```java
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.*;
import nl.jochembroekhoff.cdmlloader.annotate.*;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;

@CdmlApp
class MyApplication extends Application {
    @Cdml
    Label myLabel;
    
    @Override
    public void init() {
        CDMLLoader.init(this);
    }
    
    //...
}
```

CDMLLoader will search for fields annotated with `@Cdml` to inject the generated values.
All fields should be instantiated after `CDMLLoader.load(this)` has returned.

_To be continued._

## Writing custom components
_To do._ If you are interested, please check out the default component handlers.
They are located at `nl.jochembroekhoff.cdmlloader.defaulthandlers.component.*` and
`nl.jochembroekhoff.cdmlloader.defaulthandlers.layout.*`.

1. Creating the component itself
2. Creating the component handler
3. Registering components and additional listeners

## To Do List
- Add Component Handlers for...
    - ComboBox.Custom
- Implement...
    - Renderers
    - Internationalized notifications (now only hard-coded title and subTitle)
- Per-layout controllers
- UI Designer
- Element child items processing

## Thanks / Credits

* to [MrCrayfish](https://github.com/MrCrayfish) for the [Device Mod](https://github.com/MrCrayfish/MrCrayfishDeviceMod).