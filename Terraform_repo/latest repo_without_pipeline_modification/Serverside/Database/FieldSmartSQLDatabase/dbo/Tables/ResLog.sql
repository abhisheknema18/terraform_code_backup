CREATE TABLE [dbo].[ResLog] (
    [ReturnID]       INT          NOT NULL,
    [ResOrderNo]     INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [EditRefNo]      INT          NOT NULL,
    [UpdateUser]     VARCHAR (10) NULL,
    [UpdateDate]     INT          NULL,
    [UpdateTime]     INT          NULL,
    [EditType]       VARCHAR (1)  NULL,
    CONSTRAINT [PK_RESLOG] PRIMARY KEY CLUSTERED ([EditRefNo] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_RESLOG_IDX1]
    ON [dbo].[ResLog]([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC);

