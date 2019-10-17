#!/bin/bash
exec 0</dev/tty
RED=$(tput setaf 1)
NC=$(tput sgr0) # No Color

clear
echo ""
echo "${RED}Open RSC${NC}
Striving for a replica RSC game and more.

What would you like to do?

Choices:
  ${RED}1${NC} - Install
  ${RED}2${NC} - Update and compile code
  ${RED}3${NC} - Run game server
  ${RED}4${NC} - Exit"
echo ""
echo "Type the choice number and press enter."
echo ""
read action

if [ "$action" == "1" ]; then
  echo ""
  echo "Attempting to install the make package for Ubuntu and CentOS - one will work."
  echo ""
  sudo apt install make -y && sudo yum install make -y
  make combined-install
elif [ "$action" == "2" ]; then
  make get-updates
elif [ "$action" == "3" ]; then
  make run-server
elif [ "$action" == "4" ]; then
  exit
fi
