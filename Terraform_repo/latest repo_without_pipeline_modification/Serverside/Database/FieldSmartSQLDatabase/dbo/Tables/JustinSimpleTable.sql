CREATE TABLE [dbo].[JustinSimpleTable]
(
	[Id] INT NOT NULL PRIMARY KEY, 
    [SomeText] NVARCHAR(50) NULL DEFAULT 'hi', 
    [SomeNumber] INT NULL DEFAULT 3, 
    [SomeNewColumn] INT NULL DEFAULT 1, 
    [GerryNewColumn] INT NULL DEFAULT 99
)
