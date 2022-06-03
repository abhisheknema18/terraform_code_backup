CREATE TABLE [dbo].[ActivityLog] (
    [Usercode]  VARCHAR (10)  NOT NULL,
    [ProgCode]  VARCHAR (4)   NOT NULL,
    [LogDate]   INT           NOT NULL,
    [LogTime]   INT           NOT NULL,
    [Activity]  VARCHAR (255) NOT NULL,
    [AlertType] VARCHAR (50)  NULL
);


GO
CREATE NONCLUSTERED INDEX [I_ACTIVITYLOG]
    ON [dbo].[ActivityLog]([Usercode] ASC, [LogDate] ASC);

