FloatingAction to the best of the world and one of the best things of universe.
==============

A basic implementation of Floating Action Button pattern as seen on Material Design

![Le demo](http://i.imgur.com/Z0nTwvj.gif)

### Demo

[![FloatingAction Demo on Google Play Store](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.telly.floatingaction.demo)

### Usage

See demo, at this point latest version is `0.0.6`

```groovy
compile 'com.telly:floatingaction:(insert latest version)'
```

```java
mFloatingAction = FloatingAction.from(this)
          .listenTo(mListView)
          .icon(R.drawable.ic_action_about)
          .listener(this)
          .build();
```
