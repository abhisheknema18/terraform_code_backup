CREATE TABLE [dbo].[SubScriptTrigger] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [ScriptCode]     VARCHAR (15) NOT NULL,
    [ValidationType] VARCHAR (15) NOT NULL,
    [TriggerValue]   VARCHAR (80) NOT NULL,
    CONSTRAINT [PK_SUBSCRIPTTRIGGER] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [ScriptCode] ASC, [ValidationType] ASC, [TriggerValue] ASC)
);

