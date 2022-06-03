CREATE TABLE [dbo].[ScriptResultblb] (
    [ReturnID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [BlobResult]     IMAGE         NULL,
    [FileName]       VARCHAR (100) NULL,
    CONSTRAINT [PK_SCRIPTRESULTBLB] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC)
);

