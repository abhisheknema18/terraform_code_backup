CREATE TABLE [dbo].[DeclarationMessage] (
    [MessageId]   INT          NOT NULL,
    [MessageDesc] VARCHAR (40) NULL,
    [MessageText] TEXT         NULL,
    CONSTRAINT [PK_DECLARATIONMESSAGE] PRIMARY KEY CLUSTERED ([MessageId] ASC)
);

