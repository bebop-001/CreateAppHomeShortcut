keywords app homescreen shortcut EXTRA_SHORTCUT_INTENT ShortcutManager API26 Androi8

Fri Sep 21 11:40:37 PDT 2018

This is a simple single activity application that installs a homescreen
short cut to the users app in a way that works both pre and post API26/
Android 8.

From: https://developer.android.com/about/versions/oreo/android-8.0-changes
https://developer.android.com/about/versions/oreo/android-8.0-changes

The com.android.launcher.action.INSTALL_SHORTCUT broadcast no longer has any effect on your app, because it is now a private, implicit broadcast. Instead, you should create an app shortcut by using the requestPinShortcut() method from the ShortcutManager class.

The ACTION_CREATE_SHORTCUT intent can now create app shortcuts that you manage using the ShortcutManager class. This intent can also create legacy launcher shortcuts that don't interact with ShortcutManager. Previously, this intent could create only legacy launcher shortcuts.

Shortcuts created using requestPinShortcut() and shortcuts created in an activity that handles the ACTION_CREATE_SHORTCUT intent are now fully-fledged app shortcuts. As a result, apps can now update them using the methods in ShortcutManager.

Legacy shortcuts retain their functionality from previous versions of Android, but you must convert them to app shortcuts manually in your app.
See: https://developer.android.com/guide/topics/ui/shortcuts.html#pinning


Sat Oct 13 21:51:06 PDT 2018
Modified to use ShortcutManagerCompat and ShortcutInfoCompat.  Set flags so
Java 8 can be used and replaced button callbacks with lambds.  Got the pin
shortcut install callback working as an inner class BroadcastReceiver
inside the button callback.  The app now exits and pulls itself off the
stack when user has made their choice.

Tested on these devices:
 1) google Nexus 7       Android 6.0.1  API=23
 2) samsung SM-T820      Android 8.0.0  API=26
 3) samsung SM-T530NU    Android 5.0.2  API=21
 4) Amazon KFGIWI        Android 5.1.1  API=22
 5) lge LGL34C           Android 4.4    API=19
 6) motorola XT830C      Android 4.4.4  API=19

