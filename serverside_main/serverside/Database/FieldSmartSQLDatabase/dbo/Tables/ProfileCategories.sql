CREATE TABLE [dbo].[ProfileCategories] (
    [ProfileID]   INT NOT NULL,
    [ScriptcatID] INT NOT NULL,
    CONSTRAINT [PK_PROFILECATEGORIES] PRIMARY KEY CLUSTERED ([ProfileID] ASC, [ScriptcatID] ASC)
);

