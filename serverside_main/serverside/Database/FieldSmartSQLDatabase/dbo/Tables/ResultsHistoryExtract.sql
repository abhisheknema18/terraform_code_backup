CREATE TABLE [dbo].[ResultsHistoryExtract] (
    [ReturnID]      INT            NOT NULL,
    [ScriptID]      INT            NOT NULL,
    [ScriptCode]    VARCHAR (15)   NOT NULL,
    [ExtractDate]   INT            NOT NULL,
    [ExtractTime]   VARCHAR (6)    NOT NULL,
    [WorkGroupCode] VARCHAR (30)   NOT NULL,
    [UserCode]      VARCHAR (32)   NOT NULL,
    [DBArea]        VARCHAR (80)   NOT NULL,
    [DBClass]       VARCHAR (80)   NOT NULL,
    [FileName]      VARCHAR (1024) NOT NULL,
    CONSTRAINT [PK_ResHistoryExtract] PRIMARY KEY CLUSTERED ([ReturnID] ASC)
);

