 /*
FSSBCONN User Creation Script
--------------------------------------------------------------------------------------
 This file contains SQL statements that will create the FSSBCONN user.
 The FSSBCONN user is used by Script Builder to access the database 
 Username will be passed in as parameters from the deployment pipeline.
 There is a requirement on the SB_ADMIN role being available, so for
 this reason this script must be run after dacpac deployment.
--------------------------------------------------------------------------------------
*/
--© 2022 AMT-SYBEX GROUP LTD.

--No part of this Schema may be reproduced or transmitted, in any form or by any means or
--stored in any retrieval system of any nature without prior written permission from AMT-SYBEX Group Limited.
--
-- Script Version #{FULLSEMVER}#
--

-- ---------------------------------------
-- FSSBCON User Creation & Role Assignment
-- ---------------------------------------
IF NOT EXISTS (select * from sys.database_principals where name = '$(FSSBCON_USERNAME)')
    BEGIN
        CREATE USER [$(FSSBCON_USERNAME)] FROM LOGIN [$(FSSBCON_USERNAME)] WITH DEFAULT_SCHEMA=dbo;
        GRANT CONNECT TO [$(FSSBCON_USERNAME)];
        -- The SB_ADMIN role is setup inside the dacpac
        ALTER ROLE [SB_ADMIN] ADD MEMBER [$(FSSBCON_USERNAME)];
    END
GO

IF NOT EXISTS (select * from sys.database_principals where name = '$(FSAPP_USERNAME)')
    BEGIN
        CREATE USER [$(FSAPP_USERNAME)] FROM LOGIN [$(FSAPP_USERNAME)] WITH DEFAULT_SCHEMA=dbo;
        GRANT CONNECT TO [$(FSAPP_USERNAME)];
        ALTER ROLE [db_datareader] ADD MEMBER [$(FSAPP_USERNAME)];
        ALTER ROLE [db_datawriter] ADD MEMBER [$(FSAPP_USERNAME)];
    END
GO