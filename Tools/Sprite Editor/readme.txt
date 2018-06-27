RSCD Sprite Editor
===================
============ v1.0 =

This is a complete RSCD sprite editor. It allows the user
to unpack the existing original sprites, pack sprites and
modify the sprites.


# run_editor:
-------------------------------------------------------------------
  This loads up the sprite editor program. From here, you
  can load your custom sprites, modify their size and shift
  values and id, and re-save them. You can also see what your
  sprites will look like with colour overlays added. If you
  click the title bar button labelled 'Unpack', it will unpack
  all the original RSC sprites into the folder 'sprites' in both
  their .spr and .png formats.


# run_packer:
-------------------------------------------------------------------
  A file chooser interface will appear, select
  your existing Sprites.rscd package (or type in a new 
  file name if you're creating a brand new package), and 
  the program will import all the .spr files out of the 
  'sprites' folder and place them inside your .rscd package, 
  removing and overwriting any old sprites that have the 
  same id as the new sprite, allowing you to easily modify 
  existing sprites quickly.


# run_unpacker:
-------------------------------------------------------------------
  This will prompt you to select a .rscd file. Select your
  Sprites.rscd file, and the program will properly extract
  all your .spr and .png files into the folder called 'unpack'.


# source:
-------------------------------------------------------------------
  The source code is included, heavily commented, so feel free
  to have a play or a read.


-------------------------------------------------------------------
Note: This is not an image editing application. I suggest
      you use photoshop or a similar powerful image editing
      program. This is simply used to edit RSCD-specific
      sprite files.

Credit: All credit to Reines. All I've done is make his
        existing creation more user-friendly.

Released by: Anarchist` (Tim Creed)