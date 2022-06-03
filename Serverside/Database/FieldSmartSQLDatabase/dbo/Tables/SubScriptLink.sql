CREATE TABLE [dbo].[SubScriptLink] (
    [ReturnID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [ReturnFile]     VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_SUBSCRIPTLINK] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC, [ReturnFile] ASC)
);

