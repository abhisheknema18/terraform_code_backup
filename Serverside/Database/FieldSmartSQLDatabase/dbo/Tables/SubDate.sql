CREATE TABLE [dbo].[SubDate] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [Before]         VARCHAR (10) NULL,
    [After]          VARCHAR (10) NULL,
    [BetweenStart]   VARCHAR (10) NULL,
    [BetweenEnd]     VARCHAR (10) NULL
);


GO
CREATE NONCLUSTERED INDEX [I_SUBDATE]
    ON [dbo].[SubDate]([ScriptID] ASC, [SequenceNumber] ASC);

