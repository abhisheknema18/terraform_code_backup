CREATE TABLE [dbo].[ScoreAlert] (
    [ScriptID]    INT          NOT NULL,
    [ConsecOccur] INT          NULL,
    [AlertUser]   VARCHAR (12) NOT NULL,
    [SetupUser]   VARCHAR (12) NULL,
    [SetupDate]   INT          NULL,
    [Counter]     INT          NULL,
    CONSTRAINT [PK_SCOREALERT] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [AlertUser] ASC)
);

