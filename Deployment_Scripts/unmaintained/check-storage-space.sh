#!/bin/bash
#Check the disk usage of the root partition to ensure it is not too full.
disk_usage=$(df -h / | tail -n1 | tr -s ' ' | cut -d ' ' -f 5 | cut -d '%' -f 1)
email="openrsc.emailer@gmail.com";
discordwebhook=$(cat .discordstaffmonitorwebhook); #We are in Deployment_Scripts, not the root core directory, so we can't specify Deployment_Scripts.
# Check if the disk usage is over 90%
if [ "$disk_usage" -ge 90 ]; then
  # Send an email using the mail command
  warning="Warning: The root partition on OpenRSC $(hostname) is ${disk_usage}% full.";
  mail -s "OpenRSC $(hostname) root partition usage warning" $email <<< "$warning";
  curl -H "Content-Type: application/json" \
      -X POST \
      -d "{\"content\": \"$warning\"}" $discordwebhook 
fi
