CREATE TABLE [dbo].[SubString] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [StringCriteria] VARCHAR (80) NOT NULL,
    [GoToSeqNumber]  INT          NULL,
    CONSTRAINT [PK_SUBSTRING] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [StringCriteria] ASC)
);

