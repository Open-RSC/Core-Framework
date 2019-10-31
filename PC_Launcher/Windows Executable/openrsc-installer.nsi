!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh
 
SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide
 
OutFile "OpenRSC Launcher.exe"
Icon "icon.ico"
VIProductVersion 2.0.0.0
VIAddVersionKey ProductName "Open RSC Launcher"
VIAddVersionKey LegalCopyright "GPL v3 Open RSC"
VIAddVersionKey FileDescription "Open RSC Launcher"
VIAddVersionKey FileVersion 3.1.0
VIAddVersionKey ProductVersion "1.0.0"
VIAddVersionKey InternalName "Open RSC Launcher"
VIAddVersionKey OriginalFilename "OpenRSC Launcher.exe"
 
Section
  nsExec::Exec 'powershell -command "if (!(Test-Path "OpenRSC/OpenRSC.jar")) { Invoke-WebRequest -Uri "https://game.openrsc.com/downloads/OpenRSC.jar" -OutFile "OpenRSC/OpenRSC.jar" }"'
  nsExec::Exec 'powershell -command "cd OpenRSC; zulu8.42.0.21-ca-jre8.0.232-win_x64/bin/java.exe -jar OpenRSC.jar"'
SectionEnd