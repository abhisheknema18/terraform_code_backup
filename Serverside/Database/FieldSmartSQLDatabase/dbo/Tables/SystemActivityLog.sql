CREATE TABLE [dbo].[SystemActivityLog] (
    [UserCode]       VARCHAR (32)   NOT NULL,
    [LogDate]        INT            NOT NULL,
    [LogTime]        VARCHAR (6)    NOT NULL,
    [AppCode]        VARCHAR (4)    NULL,
    [DeviceID]       VARCHAR (255)  NULL,
    [ActivityType]   VARCHAR (20)   NULL,
    [AdditionalText] VARCHAR (1024) NULL,
    [TransFileName]  VARCHAR (255)  NULL
);


GO
CREATE NONCLUSTERED INDEX [I_SYSTEMACTIVITYLOG_USER]
    ON [dbo].[SystemActivityLog]([UserCode] ASC, [LogDate] ASC, [LogTime] ASC);


GO
CREATE NONCLUSTERED INDEX [I_SYSTEMACTIVITYLOG_ACTTYPE]
    ON [dbo].[SystemActivityLog]([ActivityType] ASC);

