CREATE TABLE [dbo].[FDCSTempResults] (
    [ID]          INT           NOT NULL,
    [ScriptCode]  VARCHAR (50)  NULL,
    [ScriptID]    INT           NULL,
    [ScriptXML]   IMAGE         NULL,
    [SummaryDesc] VARCHAR (255) NULL,
    [Status]      VARCHAR (10)  NULL,
    [ResultsXML]  IMAGE         NULL,
    [UserCode]    VARCHAR (50)  NULL,
    [WorkGroup]   VARCHAR (50)  NULL,
    [CommitDate]  INT           NULL,
    [CommitTime]  INT           NULL
);

