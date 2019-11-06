!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh
 
SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide
 
OutFile "OpenRSC Launcher.exe"
Icon "icon.ico"
VIProductVersion 2.1.0.0
VIAddVersionKey ProductName "Open RSC Launcher"
VIAddVersionKey LegalCopyright "GPLv3 Open RSC"
VIAddVersionKey FileDescription "Open RSC Launcher"
VIAddVersionKey FileVersion 2.1.0
VIAddVersionKey ProductVersion "2.1.0"
VIAddVersionKey InternalName "Open RSC Launcher"
VIAddVersionKey OriginalFilename "OpenRSC Launcher.exe"
 
Section
  nsExec::Exec 'powershell -command "cd Cache_and_OpenJRE8_Java; .\zulu8.42.0.21-ca-jre8.0.232-win_x64\bin\java.exe -jar .\OpenRSC.jar"'
SectionEnd