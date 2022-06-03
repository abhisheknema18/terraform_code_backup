CREATE TABLE [dbo].[InProgressUploadsInt] (
    [ID]            VARCHAR (36)  NOT NULL,
    [FileName]      VARCHAR (255) NOT NULL,
    [TotalSize]     INT           NOT NULL,
    [NextPart]      INT           NOT NULL,
    [ExpectedParts] INT           NOT NULL,
    [UploadStarted] INT           NOT NULL,
    CONSTRAINT [PK_InProgressUploadsInt] PRIMARY KEY CLUSTERED ([ID] ASC)
);

