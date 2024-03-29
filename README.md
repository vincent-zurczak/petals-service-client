petals-service-client
=====================

A graphical client to manually test Petals services.


Running and Debugging the User Interface
----------------------------------------

Under *test/main/java*, in the package *org.ow2.petals.client.manual*, execute the class **Main**.
This will open a dialog with the component's user interface. A mock is used in place of Petals ESB.


Compiling the Component
-----------------------

The component relies on SWT.
SWT is Java library with a part of its widgets implemented in Java (like Swing) and the other part being natives widgets (like AWT).
Therefore, this library depends on the target operating system. 

At compile time, the target OS must then be specified.
This is achieved through Maven classifiers. Here are the commands.

	mvn clean install -Dclassifier=<YOUR_OS_CLASSIFIER>


The following classifiers are currently available:

<table>
	<tr>
		<th>Operating System</th>
		<th>Maven Classifier</th>
	</tr>
	<tr>
		<td>Windows x64</td>
		<td>win32.win32.x86_64</td>
	</tr>
	<tr>
		<td>Windows x32</td>
		<td>win32.win32.x86</td>
	</tr>
	<tr>
		<td>Linux x64 with GTK</td>
		<td>gtk.linux.x86_64</td>
	</tr>
	<tr>
		<td>Linux x32 with GTK</td>
		<td>gtk.linux.x86</td>
	</tr>
	<tr>
		<td>MacOS X x64 with Cocoa</td>
		<td>cocoa.macosx.x86_64</td>
	</tr>
	<tr>
		<td>MacOS X with Cocoa</td>
		<td>cocoa.macosx</td>
	</tr>
</table>

Some others may be found at http://swt-repo.googlecode.com/svn/repo/org/eclipse/swt/


About the Icons
---------------

Icons come from:
+ http://eclipse.org (EPL)
+ http://www.gnome.org/ (GPL)
+ http://www.icons-land.com/ (Royalty-free)
