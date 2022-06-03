CREATE TABLE [dbo].[UOMNumValid] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [UOMType]        VARCHAR (12) NOT NULL,
    [UpperLimit]     VARCHAR (10) NULL,
    [LowerLimit]     VARCHAR (10) NULL,
    [UpperWarning]   VARCHAR (10) NULL,
    [LowerWarning]   VARCHAR (10) NULL,
    CONSTRAINT [PK_UOMNUMVALID] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [UOMType] ASC)
);

