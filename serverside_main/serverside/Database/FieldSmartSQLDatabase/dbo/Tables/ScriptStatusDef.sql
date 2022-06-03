CREATE TABLE [dbo].[ScriptStatusDef] (
    [ScriptCodeID]   INT          NOT NULL,
    [StatusOrderNo]  INT          NOT NULL,
    [StatusValue]    VARCHAR (15) NOT NULL,
    [SysStatusFlag]  INT          NULL,
    [BulkUpdateFlag] INT          NULL,
    [MessageId]      INT          NULL,
    CONSTRAINT [PK_SCRIPTSTATUSDEF] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [StatusOrderNo] ASC)
);

