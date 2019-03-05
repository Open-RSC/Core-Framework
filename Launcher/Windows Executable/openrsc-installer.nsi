!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh
 
SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide
 
OutFile "OpenRSC.exe"
Icon "icon.ico"
VIProductVersion 1.0.0.0
VIAddVersionKey ProductName "Open RSC Game Launcher"
VIAddVersionKey LegalCopyright "GPL v3 Open RSC"
VIAddVersionKey FileDescription "Open RSC Game Launcher"
VIAddVersionKey FileVersion 2.3.6
VIAddVersionKey ProductVersion "1.0.0"
VIAddVersionKey InternalName "Open RSC"
VIAddVersionKey OriginalFilename "OpenRSC.exe"
 
Section
  nsExec::Exec 'powershell -command "iwr -outf OpenRSC.jar https://game.openrsc.com/downloads/OpenRSC.jar"'
  nsExec::Exec '"java.exe" -jar ./OpenRSC.jar'
SectionEnd