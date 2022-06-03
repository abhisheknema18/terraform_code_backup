CREATE TABLE [dbo].[SystemUsers] (
    [UserCode]       VARCHAR (10) NOT NULL,
    [UserName]       VARCHAR (40) NOT NULL,
    [WinLogin]       VARCHAR (32) NULL,
    [DateAdded]      INT          NOT NULL,
    [SBGroupCode]    VARCHAR (10) NULL,
    [FDMGroupCode]   VARCHAR (10) NULL,
    [SMGroupCode]    VARCHAR (10) NULL,
    [Revoked]        INT          NULL,
    [AdminUser]      INT          NULL,
    [UserClass]      VARCHAR (80) NULL,
    [LastModDate]    INT          NULL,
    [LastModTime]    VARCHAR(6)          NULL,
    [HPCUserCode] VARCHAR(32) NULL, 
    CONSTRAINT [PK_SYSTEMUSERS] PRIMARY KEY CLUSTERED ([UserCode] ASC)
);

