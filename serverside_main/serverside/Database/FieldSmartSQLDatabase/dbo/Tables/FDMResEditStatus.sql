CREATE TABLE [dbo].[FDMResEditStatus] (
    [GroupCode]    VARCHAR (10) NOT NULL,
    [ScriptCodeID] INT          NOT NULL,
    [StatusValue]  VARCHAR (15) NOT NULL,
    CONSTRAINT [PK_FDMRESEDITSTATUS] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [GroupCode] ASC, [StatusValue] ASC)
);

