CREATE TABLE [dbo].[ExtdResponse] (
    [ReturnID]       INT            NOT NULL,
    [SequenceNumber] INT            NOT NULL,
    [ResOrderNo]     INT            NOT NULL,
    [ExtdResponse]   VARCHAR (1150) NULL,
    CONSTRAINT [PK_EXTDRESPONSE] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC)
);

