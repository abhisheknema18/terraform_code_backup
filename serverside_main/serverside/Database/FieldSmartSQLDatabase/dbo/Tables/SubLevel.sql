CREATE TABLE [dbo].[SubLevel] (
    [ScriptID]       INT NOT NULL,
    [SequenceNumber] INT NOT NULL,
    [LevelID]        INT NULL,
    CONSTRAINT [PK_SUBLEVEL] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

