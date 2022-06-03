CREATE TABLE [dbo].[RecordCardView] (
    [RecordCardID]   INT          NOT NULL,
    [RecordCardCode] VARCHAR (15) NOT NULL,
    [RecordCardView] VARCHAR (80) NOT NULL,
    CONSTRAINT [PK_RecordCardView] PRIMARY KEY CLUSTERED ([RecordCardID] ASC)
);

