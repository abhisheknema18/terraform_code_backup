CREATE TABLE [dbo].[MonitorAlertStatusLog] (
    [Id]         INT          NOT NULL,
    [AlertId]    INT          NOT NULL,
    [UserCode]   VARCHAR (10) NOT NULL,
    [Status]     VARCHAR (1)  NOT NULL,
    [StatusDate] INT          NOT NULL,
    [StatusTime] VARCHAR (6)  NOT NULL,
    CONSTRAINT [PK_MonitorAlertStatusLog] PRIMARY KEY CLUSTERED ([Id] ASC)
);

