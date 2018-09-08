#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework
#
# Installs and updates Open RSC
#
# Install with this command (from your Linux machine):
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/scripts/clone.sh | bash

sudo git clone https://github.com/Open-RSC/Game.git
cd Game
"./Go.sh"
