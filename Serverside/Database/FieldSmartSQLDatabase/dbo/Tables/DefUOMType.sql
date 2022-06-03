CREATE TABLE [dbo].[DefUOMType] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [DefaultType]    VARCHAR (12) NULL,
    CONSTRAINT [PK_DEFUOMTYPE] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

