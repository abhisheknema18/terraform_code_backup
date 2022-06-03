CREATE TABLE [dbo].[ScriptResultsTxt] (
    [ReturnID]       INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [FreeText]       VARCHAR (200) NULL,
    CONSTRAINT [PK_SCRIPTRESULTSTXT] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC)
);

