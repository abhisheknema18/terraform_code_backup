CREATE TABLE [dbo].[ProfileScripts] (
    [ProfileID]    INT NOT NULL,
    [ScriptCodeID] INT NOT NULL,
    CONSTRAINT [PK_PROFILESCRIPTS] PRIMARY KEY CLUSTERED ([ProfileID] ASC, [ScriptCodeID] ASC)
);

