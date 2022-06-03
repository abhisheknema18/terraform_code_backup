CREATE TABLE [dbo].[ScriptLock] (
    [ScriptID] INT          NOT NULL,
    [UserCode] VARCHAR (10) NOT NULL,
    [LockDate] INT          NOT NULL,
    CONSTRAINT [PK_SCRIPTLOCK] PRIMARY KEY CLUSTERED ([ScriptID] ASC)
);

