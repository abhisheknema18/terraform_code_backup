CREATE TABLE [dbo].[ItemFileRef] (
    [ScriptID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [FileName]       VARCHAR (100) NULL,
    [OtherRef]       VARCHAR (40)  NULL,
    [MediaId]        INT           NOT NULL,
    CONSTRAINT [PK_ITEMFILEREF] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [MediaId] ASC)
);

