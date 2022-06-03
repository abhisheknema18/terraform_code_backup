CREATE TABLE [dbo].[AutoDefect] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [Response]       VARCHAR (80) NULL,
    [DefectCode]     VARCHAR (4)  NOT NULL,
    CONSTRAINT [PK_AUTODEFECT] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [DefectCode] ASC)
);

