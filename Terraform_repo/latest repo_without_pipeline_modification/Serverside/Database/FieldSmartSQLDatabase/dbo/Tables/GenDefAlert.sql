CREATE TABLE [dbo].[GenDefAlert] (
    [ScriptId]    INT          NOT NULL,
    [Rating]      VARCHAR (2)  NOT NULL,
    [ConsecOccur] INT          NULL,
    [AlertUser]   VARCHAR (10) NOT NULL,
    [Counter]     INT          NULL,
    CONSTRAINT [PK_GENDEFALERT] PRIMARY KEY CLUSTERED ([ScriptId] ASC, [Rating] ASC, [AlertUser] ASC)
);

