CREATE TABLE [dbo].[ScriptStatusEventTypes] (
    [ScriptCodeID] INT           NOT NULL,
    [StatusValue]  VARCHAR (255) NOT NULL,
    [EventID]      INT           NOT NULL,
    CONSTRAINT [PK_ScriptStatusEventTypes] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [StatusValue] ASC, [EventID] ASC)
);

