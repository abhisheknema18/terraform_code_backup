# Introduction 
FieldSmartSQLDatabase is an SSDT type porject for FieldSmart Azure SQL Database. This project manages FieldSmart database schemas and additional sql scripts which are required to setup database into Azure Cloud.

# Getting Started
Please download and install below tool to get started with FieldSmartSQLDatabase Project:
1. SSDT with Visual Studio 2019 Community (Ref. [Link](https://docs.microsoft.com/en-us/sql/ssdt/download-sql-server-data-tools-ssdt?view=sql-server-ver15)) 
    
    License: https://visualstudio.microsoft.com/license-terms/mlt031819/ (VS 2019 Community is free for Enterprise user if we would like to use it only for SQL server development (SSDT))


# Build and Test 

1. Open the FieldSmartSQLDatabase.sln in Visual Studio 2019 Community and update the scripts based on the requirement in your feture branch code.

2. Right click on ths solution and select Build option.

3. Right click on the solution and select Publish option to publish the databse to local Database for validation


# Contribute

1. Once the changes got validated in local database / Azure SQL POC environment, raise Pull request against develop branch to publish your changes into Azure SQL Environment Databases. 
