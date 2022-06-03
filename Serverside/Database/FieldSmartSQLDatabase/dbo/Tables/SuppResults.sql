CREATE TABLE [dbo].[SuppResults] (
    [ReturnID]       INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [Response]       VARCHAR (200) NOT NULL,
    CONSTRAINT [PK_SUPPRESULTS] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [Response] ASC)
);

