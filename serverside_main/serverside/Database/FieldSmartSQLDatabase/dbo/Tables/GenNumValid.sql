CREATE TABLE [dbo].[GenNumValid] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [UpperLimit]     VARCHAR (25) NULL,
    [LowerLimit]     VARCHAR (25) NULL,
    [UpperWarning]   VARCHAR (25) NULL,
    [LowerWarning]   VARCHAR (25) NULL,
    CONSTRAINT [PK_GENNUMVALID] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

