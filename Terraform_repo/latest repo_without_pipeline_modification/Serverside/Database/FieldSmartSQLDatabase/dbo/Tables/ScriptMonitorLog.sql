CREATE TABLE [dbo].[ScriptMonitorLog] (
    [Id]            INT           NOT NULL,
    [MonitorId]     INT           NOT NULL,
    [MonitorName]   VARCHAR (30)  NOT NULL,
    [MonitorDesc]   VARCHAR (255) NOT NULL,
    [ScriptID]      INT           NOT NULL,
    [AlertType]     VARCHAR (1)   NOT NULL,
    [AlertText]     VARCHAR (40)  NOT NULL,
    [AlertMailList] VARCHAR (500) NULL,
    [MonitorStatus] VARCHAR (1)   NOT NULL,
    [Usercode]      VARCHAR (10)  NOT NULL,
    [CreateDate]    INT           NOT NULL,
    CONSTRAINT [PK_ScriptMonitorLog] PRIMARY KEY CLUSTERED ([Id] ASC)
);

