CREATE TABLE [dbo].[ResBlobLog] (
    [ReturnID]   INT           NOT NULL,
    [EditRefNo]  INT           NOT NULL,
    [ResultBlob] IMAGE         NULL,
    [FileName]   VARCHAR (100) NULL,
    CONSTRAINT [PK_RESBLOBLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditRefNo] ASC)
);

