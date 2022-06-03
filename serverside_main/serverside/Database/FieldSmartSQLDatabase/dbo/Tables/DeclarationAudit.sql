CREATE TABLE [dbo].[DeclarationAudit] (
    [ReturnID]        INT          NOT NULL,
    [MessageId]       INT          NOT NULL,
    [Status]          VARCHAR (40) NOT NULL,
    [UserCode]        VARCHAR (10) NOT NULL,
    [DeclarationDate] INT          NOT NULL,
    [DeclarationTime] INT          NOT NULL
);


GO
CREATE NONCLUSTERED INDEX [I_DECAUDIT_RETURNID]
    ON [dbo].[DeclarationAudit]([ReturnID] ASC);

