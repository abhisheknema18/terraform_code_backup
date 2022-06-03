CREATE TABLE [dbo].[SupervText] (
    [ReturnID]       INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [AddText]        VARCHAR (200) NOT NULL,
    [AddUser]        VARCHAR (10)  NOT NULL,
    [AddDate]        INT           NOT NULL,
    CONSTRAINT [PK_SUPERVTEXT] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC)
);

