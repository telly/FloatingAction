FloatingAction
==============

A basic implementation of Floating Action Button pattern as seen on Material Design

![Le demo](http://i.imgur.com/Z0nTwvj.gif)

### Usage

See demo, at this point latest version is `0.0.5`

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