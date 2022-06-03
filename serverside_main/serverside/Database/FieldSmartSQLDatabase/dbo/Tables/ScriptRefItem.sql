CREATE TABLE [dbo].[ScriptRefItem] (
    [ID]             INT         NOT NULL,
    [SequenceNumber] INT         NOT NULL,
    [IDType]         VARCHAR (1) NOT NULL,
    [RefId]          INT         NOT NULL,
    CONSTRAINT [PK_SCRIPTREFITEM] PRIMARY KEY CLUSTERED ([ID] ASC, [SequenceNumber] ASC, [IDType] ASC, [RefId] ASC)
);

