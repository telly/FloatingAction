FloatingAction
==============

A basic implementation of Floating Action Button pattern as seen on Material Design

![Le demo](http://i.imgur.com/umpHuyu.gif)

### Usage

See demo

```groovy
compile 'com.telly.floatingaction:library:(insert latest version)'
```

```java
mFloatingAction = FloatingAction.from(this)
          .listenTo(mListView)
          .icon(R.drawable.ic_action_about)
          .listener(this)
          .build();
```