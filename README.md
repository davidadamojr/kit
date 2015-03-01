# kit

KeepInTouch (kit) is an Android app that lets you set daily, weekly, biweekly and monthly reminders to call people. The app serves as a good project for learning about ContentProviders and AlarmManager.

## Bugs
The basic code for the app is written but the project is quite incomplete. There are a number of major bugs.

* When multiple reminders are set, only the most recent reminder is triggered at the specified interval. This bug definitely stems from improper use of Android Alarms/AlarmManager.
* After reminder intervals are selected from the contact list, the reminder interval labels repeat randomly down the list due to Android list item recycling. This is a UI issue.

It would be interesting if someone decides to squash these major bugs. I bet there are other bugs lurking around in there as well.

Development on this project has come to a halt. On to the next one...
