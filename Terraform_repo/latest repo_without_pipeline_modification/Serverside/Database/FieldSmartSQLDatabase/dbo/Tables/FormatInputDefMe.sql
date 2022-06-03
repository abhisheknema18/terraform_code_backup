CREATE TABLE [dbo].[FormatInputDefMe] (
    [ScriptID]       INT NOT NULL,
    [SequenceNumber] INT NOT NULL,
    [MinEntry]       INT NOT NULL,
    CONSTRAINT [PK_FORMATINPUTDEFME] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

