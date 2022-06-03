CREATE TABLE [dbo].[ScriptPassword] (
    [ScriptID]     INT          NOT NULL,
    [UserCode]     VARCHAR (10) NOT NULL,
    [PasswordDate] INT          NULL,
    [Password]     INT          NULL,
    CONSTRAINT [PK_SCRIPTPASSWORD] PRIMARY KEY CLUSTERED ([ScriptID] ASC)
);

