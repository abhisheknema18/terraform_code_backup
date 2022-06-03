CREATE TABLE [dbo].[FDMScriptAccess] (
    [GroupCode]    VARCHAR (10) NOT NULL,
    [ScriptCodeID] INT          NOT NULL,
    CONSTRAINT [PK_FDMSCRIPTACCESS] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [ScriptCodeID] ASC)
);

