CREATE TABLE [dbo].[MonitorAlerts] (
    [AlertId]     INT          NOT NULL,
    [MonitorId]   INT          NOT NULL,
    [ReturnID]    INT          NOT NULL,
    [ScriptID]    INT          NOT NULL,
    [EquipNo]     VARCHAR (30) NOT NULL,
    [AlertType]   VARCHAR (1)  NOT NULL,
    [AlertText]   VARCHAR (40) NOT NULL,
    [AlertStatus] VARCHAR (1)  NOT NULL,
    [CreateDate]  INT          NOT NULL,
    [CreateTime]  VARCHAR (6)  NOT NULL,
    CONSTRAINT [PK_MonitorAlerts] PRIMARY KEY CLUSTERED ([AlertId] ASC)
);

