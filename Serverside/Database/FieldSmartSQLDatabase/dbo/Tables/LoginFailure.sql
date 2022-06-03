CREATE TABLE [dbo].[LoginFailure] (
    [UserCode]  VARCHAR (32) NOT NULL,
    [FailCount] INT          NULL,
    CONSTRAINT [PK_LOGINFAILURE] PRIMARY KEY CLUSTERED ([UserCode] ASC)
);

