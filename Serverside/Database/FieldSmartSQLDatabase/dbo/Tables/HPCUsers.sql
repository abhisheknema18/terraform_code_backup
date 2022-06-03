CREATE TABLE [dbo].[HPCUsers] (
    [UserCode]       VARCHAR (32)  NOT NULL,
    [UserName]       VARCHAR (40)  NOT NULL,
    [AltRef]         VARCHAR (10)  NULL,
    [WorkGroupCode]  VARCHAR (10)  NOT NULL,
    [DeviceID]       VARCHAR (255) NULL,
    [Revoked]        INT           NULL,
    [LastModDate]    INT           NULL,
    [LastModTime]    VARCHAR(6)           NULL,
    [UserClass]      VARCHAR (80)  NULL,
    CONSTRAINT [PK_HPCUSERS] PRIMARY KEY CLUSTERED ([WorkGroupCode] ASC, [UserCode] ASC)
);

