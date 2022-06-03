CREATE TABLE [dbo].[ResultsHistoryExtractLog] (
    [LastFullExtractDate]  INT         NULL,
    [LastFullExtractTime]  VARCHAR (6) NULL,
    [LastDeltaExtractDate] INT         NULL,
    [LastDeltaExtractTime] VARCHAR (6) NULL,
    [LastDeltaMaxReturnID] INT         NULL
);

