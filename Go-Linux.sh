#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

installedalready=`cat .installedalready`
installmode=`cat .methodinstall`
dbuser=`cat .dbuser`
pass=`cat .pass`
email=`cat .email`

# Open RSC: A replica RSC private server framework
#
# Multi-purpose script for Open RSC
#
# Install everything with this command:
#
# bash <(curl -s https://raw.githubusercontent.com/Open-RSC/Game/2.0.0/scripts/clone.sh)

echo ""
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

What would you like to do?

Choices:
  ${RED}1${NC} - Install Open RSC
  ${RED}2${NC} - Update Open RSC
  ${RED}3${NC} - Run Open RSC
  ${RED}4${NC} - Manage Players
  ${RED}5${NC} - Perform a Hard Reset
  ${RED}6${NC} - Exit"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read action

if [ "$action" == "1" ]; then
    make combined-install
elif [ "$action" == "2" ]; then
    make get-updates
elif [ "$action" == "3" ]; then
    make run-game
elif [ "$action" == "4" ]; then
    make rank
elif [ "$action" == "5" ]; then
    make hard-reset
elif [ "$action" == "6" ]; then
    exit
fi
