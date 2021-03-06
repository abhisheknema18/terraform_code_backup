CREATE TABLE [dbo].[LWAlarmHistory] (
    [LWSESSIONID] VARCHAR (24) NOT NULL,
    [ALARMTYPE]   VARCHAR (1)  NOT NULL,
    [ACKDATE]     INT          NOT NULL,
    [ACKTIME]     INT          NOT NULL,
    [USERCODE]    CHAR (10)    NOT NULL,
    CONSTRAINT [PK_LWALARMHISTORY] PRIMARY KEY CLUSTERED ([LWSESSIONID] ASC, [ALARMTYPE] ASC, [ACKDATE] ASC, [ACKTIME] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_LWALARMHISTORY_SESSID]
    ON [dbo].[LWAlarmHistory]([LWSESSIONID] ASC);

