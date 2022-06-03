CREATE TABLE [dbo].[RespAlert] (
    [ScriptId]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [Response]       VARCHAR (100) NOT NULL,
    [AlertUser]      VARCHAR (12)  NULL,
    [SetupUser]      VARCHAR (12)  NULL,
    [Counter]        INT           NULL,
    CONSTRAINT [PK_RESPALERT] PRIMARY KEY CLUSTERED ([ScriptId] ASC, [SequenceNumber] ASC, [Response] ASC)
);

