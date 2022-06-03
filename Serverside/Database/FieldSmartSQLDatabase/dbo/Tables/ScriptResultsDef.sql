CREATE TABLE [dbo].[ScriptResultsDef] (
    [ReturnID]       INT          NOT NULL,
    [ResOrderNo]     INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [DefectCode]     VARCHAR (4)  NOT NULL,
    [Rating]         VARCHAR (2)  NOT NULL,
    [Action]         VARCHAR (1)  NOT NULL,
    [LogNo]          VARCHAR (12) NULL,
    CONSTRAINT [PK_SCRIPTRESULTSDEF] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC, [DefectCode] ASC)
);

