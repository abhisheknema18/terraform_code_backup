CREATE TABLE [dbo].[FieldActivityLog] (
    [UserCode]       VARCHAR (32)  NOT NULL,
    [LogDate]        INT           NOT NULL,
    [LogTime]        INT           NOT NULL,
    [DeviceID]       VARCHAR (255) NULL,
    [ActivityDesc]   VARCHAR (50)  NULL,
    [AdditionalText] VARCHAR (120) NULL,
    [TransFileName]  VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_FIELDACTIVITYLOG] PRIMARY KEY CLUSTERED ([UserCode] ASC, [LogDate] ASC, [LogTime] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_FIELDACTIVITYLOG_TRANSFN]
    ON [dbo].[FieldActivityLog]([TransFileName] ASC);

