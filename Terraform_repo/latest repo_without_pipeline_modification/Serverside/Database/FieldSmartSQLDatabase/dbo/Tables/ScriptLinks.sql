CREATE TABLE [dbo].[ScriptLinks] (
    [ScriptCodeID] INT          NOT NULL,
    [LinkType]     VARCHAR (1)  NOT NULL,
    [LinkedCode]   VARCHAR (18) NOT NULL,
    CONSTRAINT [PK_SCRIPTLINKS] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [LinkType] ASC, [LinkedCode] ASC)
);

