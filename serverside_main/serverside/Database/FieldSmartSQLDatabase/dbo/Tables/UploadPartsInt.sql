CREATE TABLE [dbo].[UploadPartsInt] (
    [ID]       VARCHAR (36)  NOT NULL,
    [PartNo]   INT           NOT NULL,
    [FileSize] INT           NOT NULL,
    [FileName] VARCHAR (255) NOT NULL,
    [CheckSum] VARCHAR (32)  NOT NULL,
    CONSTRAINT [PK_UploadPartsInt] PRIMARY KEY CLUSTERED ([ID] ASC, [PartNo] ASC)
);

