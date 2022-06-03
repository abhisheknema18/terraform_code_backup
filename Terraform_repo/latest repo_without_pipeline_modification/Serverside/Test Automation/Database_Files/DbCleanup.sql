--Declare a global variable used throught the query
DECLARE @SQLStmt NVARCHAR(MAX) = N''
--Disable all check constraints on all tables
SELECT @SQLStmt = @SQLStmt + COALESCE('ALTER TABLE ' + SCHEMA_NAME(schema_id) + '.' + name + ' NOCHECK CONSTRAINT ALL; ', '')
FROM sys.tables
SET @SQLStmt = 'SET NOCOUNT ON; ' + @SQLStmt
EXEC (@SQLStmt)
SET @SQLStmt = N''
 
--Disable all triggers on all tables
SELECT @SQLStmt = @SQLStmt + COALESCE(N'DISABLE TRIGGER ALL ON ' + SCHEMA_NAME(schema_id) + '.' + name + '; ', '')
FROM sys.tables
SET @SQLStmt = 'SET NOCOUNT ON; ' + @SQLStmt
EXEC (@SQLStmt)
SET @SQLStmt = N''
 
--Delete all data from all tables
SELECT @SQLStmt = @SQLStmt + COALESCE('TRUNCATE TABLE ' + SCHEMA_NAME(schema_id) + '.' + name + '; ', '')
FROM sys.tables
SET @SQLStmt = 'SET NOCOUNT ON; ' + @SQLStmt
EXEC (@SQLStmt)
SET @SQLStmt = N''
 
--Enable all constraints on all tables
SELECT @SQLStmt = @SQLStmt + COALESCE('ALTER TABLE ' + SCHEMA_NAME(schema_id) + '.' + name + ' CHECK CONSTRAINT ALL; ', '')
FROM sys.tables
SET @SQLStmt = 'SET NOCOUNT ON; ' + @SQLStmt
EXEC (@SQLStmt)
SET @SQLStmt = N''
 
--Enable all triggers on all tables
SELECT @SQLStmt = @SQLStmt + COALESCE(N'ENABLE TRIGGER ALL ON ' + SCHEMA_NAME(schema_id) + '.' + name + '; ', '')
FROM sys.tables
SET @SQLStmt = 'SET NOCOUNT ON; ' + @SQLStmt
EXEC (@SQLStmt)
