CREATE TABLE [dbo].[ItemRespRulesRef] (
    [ScriptID]       INT NOT NULL,
    [SequenceNumber] INT NOT NULL,
    [RefSeqNo]       INT NOT NULL,
    CONSTRAINT [PK_ITEMRESPRULESREF] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

