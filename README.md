# RobotCA
#### ROS-autom Team

This project is active again.

###### Prepare your distro for compile:

+ Download Android Studio from https://developer.android.com/studio/index.html#linux-bundle
+ Run the bin/studio.sh script
+ Follow the introduction until you reach the IDE
+ Save the SDK to /home/android_sdk
+ Open SDK Manager (Press the icon with blue arrow and android logo on tool bar)
+ From SDK platforms select all API levels from 26 to 22
+ From SDK Tools select 'Show package details'
+ Select 21.1.2 and 26.0.0 
+ Press apply and ok
+ Accept the license and let it download the required files
+ cd to the husky_kinetic/RobotCA/src/android_foo and run "./gradlew build"

#### University of South Carolina  
###### CSCE 490 & 492 Capstone Design  

Our project is to design an Android application to control a robot. Specifically, the goal is to create a native Android tablet application to interface with a Robot Operating System (ROS) robot, Clearpath Robotics' HUSKY model, for direct drive, diagnostic, and navigation information. The application will have to process data from various sensors on the robot and display the information in a user understandable fashion. For example, the robot will have a laser ranging system which will be collecting data on the environment. The application must parse this data and display the environment that the robot "sees".

#### [Download from the Google Play Store today!](https://play.google.com/store/apps/details?id=com.robotca.ControlApp)

Dependencies:  
+ ROS
+ ROSJava
+ Android 4.0+ (API level 13+)

![Master Chooser](https://cloud.githubusercontent.com/assets/8508489/14839465/021d5f80-0bf9-11e6-9580-10fa54de7cfc.png) 

![Main Layout](https://cloud.githubusercontent.com/assets/8508489/14839460/0201419c-0bf9-11e6-82c9-8e51ce85d48c.png)  

Client Information:  
Dr. Ioannis Rekleitis  
Assistant Professor  
University of South Carolina  
