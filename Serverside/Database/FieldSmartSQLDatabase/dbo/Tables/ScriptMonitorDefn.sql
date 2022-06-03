CREATE TABLE [dbo].[ScriptMonitorDefn] (
    [MonitorId]      INT          NOT NULL,
    [PatternType]    VARCHAR (3)  NOT NULL,
    [Item]           VARCHAR (30) NOT NULL,
    [Op]             VARCHAR (3)  NULL,
    [Value]          VARCHAR (80) NOT NULL,
    [Occurrences]    INT          NULL,
    [TimelineDays]   INT          NULL,
    [DeviationCheck] VARCHAR (5)  NULL,
    CONSTRAINT [PK_ScriptMonitorDefn] PRIMARY KEY CLUSTERED ([MonitorId] ASC)
);

