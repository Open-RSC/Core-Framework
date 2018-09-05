#!/bin/bash

sudo mkfs -t ext4 /dev/xvdf
sudo mkdir /mnt/data-store
sudo mount /dev/xvdf /mnt/data-store

echo "/var/lib/docker /mnt/data-store bind defaults,bind 0 0" | sudo tee -a /etc/fstab
