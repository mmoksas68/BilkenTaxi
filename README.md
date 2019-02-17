# BilkenTaxi
Android Studio project

Title: BilkenTransportation

Description: 
In this application, it is aimed to solve Bilkenters’ some transportation problems.
Through the application, BilkenTransportation, opportunity of hitchchiking easily will be provided to Bilkenters.
In BilkentTransportation, Bilkenter will sign in the system by using their Bilkent e-mail adresses and passwords and they will be able to sign in as the driver mode or passenger mode depending on the situation whether they have a car or not at that moment.
In this way, if the user is a passenger, she/he can see online drivers and their directions in the map.
If the user wishes, a request to contact can be sent.
When the driver accepts this request, drivers’ phone number will be shared by the passenger and they can contact each other independently
from the app and go to the desired location together.
As a result, through BilkenTransportation, Bilkenters will be able to hitchhike in the online platform.

Current Status:
> We've almost finished main parts of our application.
- We created the following classes so that users can sign up and sign in the application and data is stored in the server.
- Also, through the classes (Maps Activity, Make Request, New Request) including hitchhiking process, passengers can send a request to driver and drivers can accept or deny it.
- Additionally, BilkenTransportation includes some significant landmarks in Bilkent so that new Bilkenters can easily find desired buildings in Bilkent.
- We solved problem about blocking users so, users can blocked other users if they want.
- We also finished communication part so that when one user makes a request to  a driver and  when the driver accepts user's request user can make a call to the driver.
- In the map, We showed drivers' destination.

> Briefly, what has been done so far can be summed up as in the above:
* Sign in, Sign up, Profile, Search Profiles, Edit Classes: Hamza Pehlivan & Doðukan Köse
* Maps Activity, Make Request, New Request Classes, MyProfile: Musab Okþaþ & Fatih Uyanýk
* General Interface Design, Bilkent Landmarks and Information classes, Navigationbar: Aybüke Ertekin & Meryem Efe

Used Tools:
> We used 5 main tools to do the application
- Android Studio
- SQL Lite
- Amazon Web Services for server
- Parse open source code
- GoogleMaps 

Organizing Classes:
When we are distributing tasks we distributed unconnected parts to each group members in that way everyone has done own code. 
But at some point, some classes are connected with other classes. 
In these parts, group members got together and tried to organize these classes. 
After everyone has done own classes, we uploaded all classes to Dropbox and Fatih tried to put all classes together and fixed compliance issues. 

To Run Application:
You should extract documents from the zip file then open android studio. 
After open android studio, you should open a new project and open BilkenTransportation app. 
For compiling codes, you need to clean project and rebuild it, so in order to clean and rebuild project, you should click build options on the toolbar of the android studio. 
After cleaning and rebuilding process you can run the program from the toolbar with the run button.
When you run program, android studio wants you to choose an emulator, you should choose Pixel 2 XL API virtual machine, for seeing user interface properly.
