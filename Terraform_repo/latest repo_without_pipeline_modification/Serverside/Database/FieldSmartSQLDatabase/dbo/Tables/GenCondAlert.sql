CREATE TABLE [dbo].[GenCondAlert] (
    [ScriptId]    INT           NOT NULL,
    [CondType]    VARCHAR (20)  NOT NULL,
    [Response]    VARCHAR (100) NOT NULL,
    [ConsecOccur] INT           NULL,
    [AlertUser]   VARCHAR (10)  NOT NULL,
    [Counter]     INT           NULL,
    CONSTRAINT [PK_GENCONDALERT] PRIMARY KEY CLUSTERED ([ScriptId] ASC, [Response] ASC, [CondType] ASC, [AlertUser] ASC)
);

