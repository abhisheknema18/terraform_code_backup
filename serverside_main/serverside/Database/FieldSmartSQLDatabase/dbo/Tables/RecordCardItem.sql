CREATE TABLE [dbo].[RecordCardItem] (
    [RecordCardItemID]    INT          NOT NULL,
    [RecordCardID]        INT          NOT NULL,
    [RecordCardItemDesc]  VARCHAR (80) NOT NULL,
    [RecordCardItemOrder] INT          NOT NULL,
    [Heading]             INT          NOT NULL,
    [RefID]               INT          NULL,
    CONSTRAINT [PK_RecordCardItem] PRIMARY KEY CLUSTERED ([RecordCardItemID] ASC)
);

