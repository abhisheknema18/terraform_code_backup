CREATE TABLE [dbo].[ResultStatusLog] (
    [ReturnId]     INT          NOT NULL,
    [ResultStatus] VARCHAR (15) NOT NULL,
    [AquiredDate]  INT          NULL,
    [CheckFlag]    INT          NULL,
    [UserCode]     VARCHAR (32) NULL,
    [AquiredTime]  INT          NULL
);


GO
CREATE NONCLUSTERED INDEX [I_RESULTSTATUSLOG_RESSTATUS]
    ON [dbo].[ResultStatusLog]([ResultStatus] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RESULTSTATUSLOG_RTNID]
    ON [dbo].[ResultStatusLog]([ReturnId] ASC);

