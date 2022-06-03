#!/bin/sh
echo "*************Running entry.sh file**********"
pwd
ls
echo "Starting JMeter tests on ${URL}"
mkdir code
chmod +x code
chmod +x apache-jmeter-5.4.1
cd apache-jmeter-5.4.1/bin
pwd

## Pass scenarios

sh jmeter -p /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/userdata.properties -n -t /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/Endpoint_integration_workorder_Dispatch_workOrderNo.jmx -l /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/code/testdata.jtl
sh jmeter -p /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/userdata.properties -n -t /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/Endpoint_integration_workorder_issue_workOrderNo.jmx -l /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/code/testdata.jtl
sh jmeter -p /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/userdata.properties -n -t /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/Endpoint_integration_workorder_release_workOrderNo.jmx -l /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/code/testdata.jtl

# Creating report based out of executed JMeter Test.
sh jmeter -g /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/code/testdata.jtl  -f -o /opt/apache-jmeter-5.4.1/apache-jmeter-5.4.1/bin/code/reports

echo "********entry.sh file RAN SUCCESSFULLY*******"