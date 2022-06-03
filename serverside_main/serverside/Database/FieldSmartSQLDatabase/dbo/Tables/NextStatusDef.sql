CREATE TABLE [dbo].[NextStatusDef] (
    [ScriptCodeID]    INT          NOT NULL,
    [StatusValue]     VARCHAR (15) NOT NULL,
    [NextStatusValue] VARCHAR (15) NOT NULL,
    [StatusOrderNo]   INT          NOT NULL,
    CONSTRAINT [PK_NEXTSTATUSDEF] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [StatusOrderNo] ASC, [StatusValue] ASC)
);

