CREATE TABLE [dbo].[SubNumber] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [GreaterThan]    VARCHAR (10) NULL,
    [LessThan]       VARCHAR (10) NULL,
    [BetweenMax]     VARCHAR (10) NULL,
    [BetweenMin]     VARCHAR (10) NULL,
    [TypeID]         INT          NULL,
    CONSTRAINT [PK_SUBNUMBER] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

