-- Use following sample command to setup database
-- sqlcmd -S sql-web-dev-uksouth.database.windows.net -d fieldsmart-dev -U fieldsmartadmin -P Fieldsmart@123Fieldsmart@123 -i ./db-setup.sql

USE master;  
GO  
-- create SQL login in master database
CREATE LOGIN DevLogin
WITH PASSWORD = 'FieldsmartMobileDev@123';

--------------------------------------------------------------
USE "fieldsmart-dev";  
GO  

CREATE USER [DevLogin]
FROM LOGIN [DevLogin]
WITH DEFAULT_SCHEMA=dbo;
GO

-- add user to database role(s) (i.e. db_owner)
ALTER ROLE db_datareader ADD MEMBER [DevLogin];
ALTER ROLE db_datawriter ADD MEMBER [DevLogin];
GO

EXECUTE sp_set_database_firewall_rule N'Allow AKS South', '172.16.8.0', '172.16.15.255';
EXECUTE sp_set_database_firewall_rule N'Allow AKS West', '10.16.8.0', '10.16.15.255';
EXECUTE sp_set_database_firewall_rule N'Allow SB', '172.16.4.32', '172.16.4.63';