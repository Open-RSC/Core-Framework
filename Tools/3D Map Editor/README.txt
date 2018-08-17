RSCD MapEditor - by DeadHeadedZed
based on the RSCA 3D Map Editor by Peeter (https://code.google.com/p/rscamap3deditor/)

==================================================================================
Liscensed under GPL v3

This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

===================================================================================

Double-click "MapEditor.jar" to start the editor - there are no ANT scripts to run.

Load a section; h0x51y51 is Lumbridge, this will put you into the main screen of the editor.

Despite having multiple files in the "edit" directory included, only the Landscape.rscd file is ever changed.
When you need to move your landscape file to your client it is the only file you will need to copy.

Main Screen
========================================================
The drop-down dialog menus are on the top toolbar, more on them below.

After you've loaded a coordinate the editor will load an area of 2x2 sections.

There are 3 print-outs in the top-left that show important information.
Mouse: The local coordinate of the tile your mouse is over.
World: The world coordinate of the tile your mouse is over - helpful for placing new objects.
Facing: The direction the camera is facing.

-----
Navigating the loaded sections
-----
Use WASD to move the camera, and arrow keys to rotate/zoom.

==============================================================
Using the land editor
------------------------
Change the "Mode" to "Land" from the drop-down. This will open the land editing dialog.

I really haven't made many changes to how this works yet, set up the properties you want the tile to have and click the "Apply" button.
Then when you left-click on a tile in the editor that tile will take on the properties you have set for it.

You can copy the properties of a tile into the land editor dialog by right-clicking a tile. To use these settings for another tile make sure you hit the "Apply" button.

=========================================================
Dialog Menus
--------------------------
File
------
OPEN - Loads a new section for editing from the Landscape.rscd file inside the "edit" directory, 
	does not save any previous work you have done on the currently loaded sections.

SAVE - Will save your current work to the Landscape.rscd file inside the "edit" directory.

EXIT - Close program.

-----
Editor
-----
RELOAD SECTION - Reverts any unsaved changes you have made to the currently loaded sections.

-----
Move
-----
Any of these buttons will move you 1 section in the direction you have chosen so you can more easily navigate the map.

-----
View
-----
TOGGLE ROOFS - Toggle being able to see roofs in the editor or not.

-----
Mode
-----
VIEW ONLY - Any click you make on the editor screen will not change anything. Basically, read-only mode.

LAND - Clicking this will open the tile editor dialog.

-----
Help
-----
Not much here.

================================================
Settings.ini
------------------
Not really much to mess with here.

"client_dir" - will change the working directory the editor uses to load and save the Landscape.rscd file, as well as where to load the textures, etc.

"height_y_shift" - This number is the offset the world uses to display the world coordinates when you move up and down between heights.
			It has no effects on the editor except the "World" info print-out. Doesn't need to be changed unless you are reprogramming the client.


