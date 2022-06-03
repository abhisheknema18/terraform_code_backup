CREATE TABLE [dbo].[UploadParts] (
    [ID]       VARCHAR (36)  NOT NULL,
    [PartNo]   INT           NOT NULL,
    [FileSize] INT           NOT NULL,
    [FileName] VARCHAR (255) NOT NULL,
    [CheckSum] VARCHAR (32)  NOT NULL,
    CONSTRAINT [PK_UploadParts] PRIMARY KEY CLUSTERED ([ID] ASC, [PartNo] ASC)
);

