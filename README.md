BangFeatures:
-Large Distributed Shared Persistent 3D Space (bangSpace) using Jini/JavaSpaces.
-Sector system for handling loading and unloading of chunks of space/geometry
-bangMaps, navigational tool for navigating 2D websites in 3D
-Meta-Authoring, insert VRML97, J3D, SpaceMark & bangMaps into the space and build your own worlds. Currently you can only insert objects. This is not intended to author geometry, but as a space assembly tool.
- Cross platform, written in Java 2 and Java3D.
- VRML97 support for worlds and avatars using..
- Voice navigation and chat with JavaSpeech 1.0.
- Video chat using RTP through JMF1.1 FCS.
- Support for native Java3D file format trough the LoadInterface3D class.
- Shared avatars using IP-Multicasting, VNet and/or DeepMatrix.
- Full screen desktop version included.
- Multiuser 3D maps of the Internet
- Client for MOO with text editor.
Release notees/Change log:
13/03/1999 Bang 1.1 Alpha:
We have just managed to get the Jini/JavaSpaces services up and running on the public
Internet.
The server is unstable, running pre1 of Java2 JDK1.2 on Linux. We'll be clearing the
persistent database all the time so if you build something beautiful don't expect it to
persist.
New Features:
-Large Distributed Shared Persistent 3D Space (bangSpace)
-Sector system for handling loading and unloading of chunks of space/geometry
-bangMaps, navigational tool for navigating 2D websites in 3D
-Meta-Authoring, insert VRML97, J3D, SpaceMark & bangMaps into the space and
build your own worlds. Currently you can only insert object, but in beta2 you'll be
able to move/rot/scale all object entries. This is not intended to author geometry, but as
a space assembly tool.
-Memory monitor with history graph
-Bang now includes the Jini implementation from Sun Microsystems
The persistent model is built using Jini and JavaSpaces.
Current todo list:
-Fix the 3D compass
-Implement the new VNet and extend it to support the Sector system for shared behaviors
-Implement the Sector unloading part (garbage collection)
-Select and Delete objects from bangSpace.
-...
-..
-.
20/01/1999 Bang 1.0:
-Features
 Multi-user 3D shared spaces.
 Cross platform, written in Java 2 and Java3D.
 VRML97 support for worlds and avatars using..
 Video chat using RTP through JMF1.1 FCS.
 Support for native Java3D file format trough the LoadInterface3D class.
 Shared avatars using IP-Multicasting, VNet and/or DeepMatrix.
 Full screen desktop version included.
 Jini/JavaSpaces based webcrawlers that create multiuser 3D maps of the Internet
 Voice navigation and chat with JavaSpeech 1.0.
 Client for MOO with text editor.
13/03/1999 Bang Beta 4:
J3D.classfileformatfor3D content/scenes/worlds added using the
LoadInterface3D.class. This enables you to build interactve 3D content
using Java1.2 & Java3D, loaded over the web as .class files.
You can deploy any of the J3D loaders in those applications/scenes,
so many new geometry format can now be used in addition to VRML97. You
can also write your own geometry loaders for special purpose applications.
support for HeadsUpDisplay added.
01/01/1999 Bang Beta 3:
- Features added/Bugs fixed:
Support for RTP real-time video chat trough Java Media Framework1.1 FCS
Desktop version of bang added, that will take over your
screen and run bang under JDesktopPane ;) This is a option only
there is also the good old JFrame version.
- Initial support for Jini/JavaSpaces.
The method saveVRML added, that will go out on the net using a URL,
load and parse a VRML97 file and upload the 3D content and behaviors
into JavaSpaces as a compressed Java3D BranchGroup object.
Then there is the method loadRoom3D that contacts the JavaSpaces server
and loads the compressed Java3D BranchGroup object into the live scene
graph.
This should result in fast loading of (former) VRML content,
but its broken ;( because Java3D Objects are not serializable yet.
I've included this for you to hack on, Jini/JavaSpaces is a seriously
interesting subject in the 3D context.
20/12/1998 Bang Beta 2:
In addition to VRML97, bang now supports native Java3D .class
objects, downloadable from web servers for worlds and MOO
objects. You will have to implement the LoadInterface3D to
your application before bang can load it. Also your root
17/12/1998 Bang Beta 1:
Changed name to Bang.
I´ve only tested this with the new IBM JavaSpeech implementation and ViaVoice98 but it works really well ;-)
I´ve only implemented some basic 3D voice navigation, yet. Controlling your journey, trough 3D worlds using your voice is a new dimension all together and very hard to describe in words.
Here are the voice commands that have been implemented after 1 day of hackin'
       Turning left: Turn left/Move left/Left
       Turning right: Turn right/Move right/Right
       Move forward: Move forward/Forward/Walk/Go forward
       Move backwards: Move back/Back/Backwards/Go back
       Run: Run/Faster
       Stop: Halt/Stop
09/12/1998 JavaMOO Alpha 5:
- Features added/Bugs fixed:
Enhanced for JDK1.2 (Java 2.0) and J3D1.1 (Final release).
3D Sound Support.
Swing popup-menus on Inventory and Contents tab panels.
Noteditor in Swing added for MOO text objects.
Few small bugs fixed.
21/11/1998 JavaMOO Alpha 4:
- Features added/Bugs fixed:
You can now change your avatar-type in the "me" panel.
The client does not crash now when loading bad VRML97 scenes,
in fact there are error messages in 3D ;)
Various compatibility issues solved involving smooth transfer of
info between the Java3D scene-graph and the standard browser based
VRML97 scene-graph when using multi-user services.
Lots of other bug fixes.
13/11/1998 JavaMOO Alpha 3:
Features added:
-DeepMatrix support
You can now connect MOO rooms and objects to DeepMatrix MU servers.
There is currently only support for object/avatar position, rotation and chat.
This is the 3rd VRML MU standard that JavaMOO supports in addition to VNet and IP-Multicast.
-Viewpoint changer
Use Swing in the me panel to change your viewpoint.
06/11/1998 JavaMOO Alpha 2:
Features added:
-VNet support
You can now connect MOO rooms and objects to VNet servers.
For example, you can do: @move me to #237
This will teleport you to the vrmLab Town Square and you can
meet/chat with other users/avatars even if they are not running
Java3D but the NS/IE/Cosmo/VW/VNetClient combo.
-Me panel
Now you can adjust your headlight volume, scale your avatar and
change your gender using the JavaMOO Swing GUI ;)
-Keyboard arrow keys for 3D movement left+right/forward+back.
You can change those keyboard shortcuts easily by hacking the
KeyboardBehavior.java file and recompile it.
21/10/1998 JavaMOO Alpha 1:
This is primarily a Moo client using VRML97, HTML and/or Flash to
describe how Moo objects look like. We intend to use this system
for developing multi-user role-playing games and educational
applications for the Internet.
Features
Client for LambdaMoo (Moo stands for MUD (Multi-user Dungeons &
Dragons) Object Oriented)
Java3D support
VRML97 support for worlds and avatars using the VRML97Loader class
Multi-user position support for avatars using ip-multicast
GNU copyright (full source code included)
History
JavaMoo was born on 1. April 1998 as an Java applet to work in a
(HTML) Browser with a VRML plugin and using the VNet multi-user
avatar system. Next JavaMoo evolved into using Flash applets to
describe the Moo spaces in a cartoon like fashion. Now after attending
(remotely) a Java3D presentation @ the VRML LA User Group - I decided
to make an Java3D version, and here it is.
--Installation
Download
For the client download:
 JDK1.2beta4  from http://developer.java.sun.com
 Java3Dbeta2  from http://developer.java.sun.com
 VRML97beta2  from http://developer.java.sun.com
 JavaMoo      from http://this.is/javamoo
If you have Win32 system you can select between an opengl and
a direct3d version, please use the opengl version because of some
nasty rendering bugs in the direct3d implementation.
For the server download and install:
LambdaMoo    from ftp://ftp.lambda.moo.mud.org/pub/MOO/
Install
Install JDK1.2
Install Java3D
Install VRML97
Use the policytool to set networks access for the internet host
and.this.is and the ipmulticast.
Uncompress the javamoo.zip archive somewhere
type:
cd javamoo
java Moo
--User guide (well...)
Login
You login by typing create   into the textfield
at the bottom of the screen. This will create you as an user.
Use @gender 
to set your gender
Use @describe me as 
3D Movement
Left mouse button: turns you around
Right mouse button: up&down and slide
Alt+Left mouse button: walk and turn
Moo Space Movement
You use standard english/... like lingo: "north", "south", "east",
"west" ... depending on names of the Moo spaces.
Communications
say("), whisper, page, emote, gag, whereis, who, ...
Manipulation
get, drop, give, put, look, inventory, ...
Building
@dig, @create, @recycle, @quota, @count, @audit, @classes, @realm,
@move, @descibe, @show, @list, @edit ...
Mail
@mail, @read, @next, @prev, @send, @answer, @forward, ...
News
@rn, @subscribe, @skip, @unsubscribe, ...
javaMoo commands (not part of the original LambdaMoo core)
connect   object to URL
vconnect  object to VRML97 URL
vconnects object to IP multicast channel
vconnectp object to IP multicast channel port
--How to create your own 3D Moo space
first drop me a note @ -email is unavailable- and I´ll authorize you as a
programmer
then, type the following commands into the textfield at the bottom of
the screen:
@dig My First Moo Space
<this output will be generated by the Moo:
My First Moo Space (#objectnr) created.
<Write down the #objectnr because that is the reference to your
<space, now keep on typing
@move me to #objectnr
vconnect #objectnr to http://url.for.your.vrml.file/file.wr*
vconnects #objectnr to x.x.x.x (an legal multicast channel)
vconnectp #objectnr to xxxx (an legal multicast port)
@describe #objectnr as A happy space with sounds of seagulls in the
distance.
@move me to #62
@move me to #objectnr
<now your VRML97 scene should be loading, note: the VRML97Loader class
has
<limited VRML97 support and will crash the javaMoo upon unsupported tags
--Known bugs/limitations
Sometimes when you mess around with the JSplitPanes you get
some Swing related errors.
The html and flash part of the system are not included in this
version.
All viewpoints are loaded but there is not yet a mechanism for
changing between them. You will always enter the scene @ the first
described viewpoint in the VRML97 file.
When you move up or down in a 3D scene you will always be
transformed back to the initial ground level when you go forward again.
The avatars 3D positions are centered in the middle.
The Inventory panel is not activated until you first type in the
i*nventory Moo command.
The source code is a kind of a hack. Not very well commented and some
unused objects here and there.
The TTL for the ipmulticast channel is hard coded to 10, I´ll add
another Moo command to set this options for objects.
Our host and.this.is is not on a very large Internet connection.
JavaMoo has only been tested on a Windows98 system not any Sun
hardware.
--Tips
Use DEF AVATAR for the root Transform for you avatar if you are
using your own.
To change your avatar, type: connect me to
http://the.url.of.your.avatar/file.wr*
You will have to move between spaces to active the change. (will be
fixed)
Type "home" into the textfield to return to the main "hub" VRML
scene.
