CREATE TABLE [dbo].[DefectAlert] (
    [ScriptId]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [DefectCode]     VARCHAR (4)  NOT NULL,
    [AlertUser]      VARCHAR (12) NULL,
    [SetupUser]      VARCHAR (12) NULL,
    [Counter]        INT          NULL,
    CONSTRAINT [PK_DEFECTALERT] PRIMARY KEY CLUSTERED ([ScriptId] ASC, [SequenceNumber] ASC, [DefectCode] ASC)
);

