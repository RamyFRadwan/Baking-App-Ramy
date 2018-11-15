# baking-app

This is the third project for the Udacity Android Developer Fast Track supported by Google. The project meet the requested specifications.

### General App Usage
1. Display recipes. App should display recipes from provided network resource.
2. App Navigation. App should allow navigation between individual recipes and recipe steps.
3. Utilization of RecyclerView. App uses RecyclerView and can handle recipe steps that include videos or images.
4. App conforms to common standards found in the [Android Nanodegree General Project Guidelines](http://udacity.github.io/android-nanodegree-guidelines/core.html).

### Components and Libraries
1. Master Detail Flow and Fragments. Application uses Master Detail Flow to display recipe steps and navigation between them.
2. Exoplayer(MediaPlayer) to display videos. Application uses Exoplayer to display videos.
3. Proper utilization of video assets. Application properly initializes and releases video assets when appropriate.
4. Proper network asset utilization. Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
5. UI Testing. Application makes use of Espresso to test aspects of the UI.
6. Third-party libraries. Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with ContentProviders if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

### Homescreen Widget
1. Application has a companion homescreen widget.
2. Widget displays ingredient list for desired recipe.