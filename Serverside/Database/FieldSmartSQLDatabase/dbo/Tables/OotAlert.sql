CREATE TABLE [dbo].[OotAlert] (
    [ScriptId]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [AlertUser]      VARCHAR (12) NOT NULL,
    [SetupUser]      VARCHAR (12) NULL,
    [Counter]        INT          NULL,
    CONSTRAINT [PK_OOTALERT] PRIMARY KEY CLUSTERED ([ScriptId] ASC, [SequenceNumber] ASC, [AlertUser] ASC)
);

