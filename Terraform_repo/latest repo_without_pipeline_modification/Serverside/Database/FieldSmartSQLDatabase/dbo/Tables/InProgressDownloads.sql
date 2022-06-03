CREATE TABLE [dbo].[InProgressDownloads] (
    [ID]              VARCHAR (36)  NOT NULL,
    [ExpectedParts]   INT           NOT NULL,
    [DownloadStarted] INT           NOT NULL,
    [FileURI]         VARCHAR (255) NOT NULL,
    [DBFile]          INT           NOT NULL,
    CONSTRAINT [PK_InProgressDownloads] PRIMARY KEY CLUSTERED ([ID] ASC)
);

