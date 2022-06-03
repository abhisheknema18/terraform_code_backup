CREATE TABLE [dbo].[ItemRespRules] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [PrevResponse]   VARCHAR (80) NOT NULL,
    [ValidationType] VARCHAR (15) NOT NULL,
    CONSTRAINT [PK_ITEMRESPRULES] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [ValidationType] ASC, [PrevResponse] ASC)
);

