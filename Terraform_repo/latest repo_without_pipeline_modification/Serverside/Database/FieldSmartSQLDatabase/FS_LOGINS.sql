--
-- Script Version #{FULLSEMVER}#
-- 


-- --------------------------------------------
-- FSSBCON Login Creation - Script Builder User
-- --------------------------------------------

IF NOT EXISTS (SELECT name FROM sys.sql_logins WHERE name='$(FSSBCON_USERNAME)')
    BEGIN
       CREATE LOGIN [$(FSSBCON_USERNAME)] WITH PASSWORD = '$(FSSBCON_PASSWORD)';
    END
ELSE
    ALTER LOGIN [$(FSSBCON_USERNAME)] WITH PASSWORD = '$(FSSBCON_PASSWORD)';
GO

IF NOT EXISTS (SELECT name FROM sys.sql_logins WHERE name='$(FSAPP_USERNAME)')
    BEGIN
       CREATE LOGIN [$(FSAPP_USERNAME)] WITH PASSWORD = '$(FSAPP_PASSWORD)';
    END
ELSE
    ALTER LOGIN [$(FSAPP_USERNAME)] WITH PASSWORD = '$(FSAPP_PASSWORD)';
GO