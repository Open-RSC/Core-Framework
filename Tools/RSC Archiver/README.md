#DeadHeadedZed's RSC Archiver
**Requires Java 8**

This is a GUI frontend for opening and editing RSC archives. 
This program can import, export, delete, and replace archived files. 
You can create new empty archives, and also search for files within them.

**This program does not covert common formats into formats used by RSC or vise-vera.** 
EG: it does not convert OB3 -> OBJ etc. All data exported will be in the raw format, albeit uncompressed from 
the BZ2 system these archive use.

#Downloads
Executable JARs are are located in the "/builds" directory.

#Help
##Saving
**Changes you make to an archive will not be written to disk until you hit save!**

There are warning boxes if you close the program without saving.
##Importing
The **"Import..."** menu item allows for multiple items to be selected and imported at one time,
 there is collision checking and this allows you to skip, overwrite, or overwrite all files that may collide 
 with files already existing in the archive.

The **"Replace Single..."** menu item allows you to replace a certain hashed file inside the archive that may 
not correspond to the name of the file you are importing. You can read more about how files are hashed inside the archive below in the **"about hashes"** section.

##Exporting
The **"Export Single As..."** menu item allows you to export an archived file with a name and extension of your choosing.

The **"Export Selected..."** menu item will export all selected items to the directory of your choosing, 
but will use their hash as their file name. They will not have an extension.

##Searching
The **"Search By Filename"** menu item allows you to type in a file *with extension* to search for. 
It will highlight if found. This is done by taking the string you type in and hashing it using the same 
technique use by the archive, then searching for that hash.

The **"Search By Hash"** menu item will search the archive for that particular hash.

##About Hashes
Each file you import into an archive uses a hash instead of a filename to be identified. 
These hashes are 24-bit integers, and are the numbers listed when you open an archive. 
Due to their nature they cannot always be 100% recreated as text-strings.

When you import a file the hash is created from the complete filename of the imported file. 
EG: if you import a file called "test.txt" it will be hashed to: -741670958

To find this file again you can either **Search By Filename** for "test.txt" or **Search By Hash** for "-741670958". 
Of course if it was the last file imported, it will be the last file in the list.