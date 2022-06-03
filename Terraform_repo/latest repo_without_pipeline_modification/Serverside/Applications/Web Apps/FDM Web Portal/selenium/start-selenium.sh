#!/bin/bash
mvn test
sleep 5s # Wait 5 seconds before continuing
sudo cp -r reports /opt/selenium_reports
if [ -d "/opt/Portal_Selenium/Selenium_App/screenshots" ] 
then
    echo "Directory /path/to/dir DOES NOT exists." 
    sudo cp -r screenshots /opt/selenium_reports
fi