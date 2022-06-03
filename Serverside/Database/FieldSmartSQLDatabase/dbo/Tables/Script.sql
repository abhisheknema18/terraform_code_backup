CREATE TABLE [dbo].[Script] (
    [ScriptCodeID]   INT           NOT NULL,
    [ScriptCode]     VARCHAR (15)  NOT NULL,
    [ScriptCatId]    INT           NOT NULL,
    [ScriptDesc]     VARCHAR (100) NOT NULL,
    [StandAlone]     INT           NULL,
    [CalcScore]      INT           NULL,
    [ScoreThreshold] INT           NULL,
    CONSTRAINT [PK_SCRIPT] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_SCRIPT_SCRIPTCATID]
    ON [dbo].[Script]([ScriptCatId] ASC);


GO
CREATE NONCLUSTERED INDEX [I_SCRIPT_SCRIPTCODE]
    ON [dbo].[Script]([ScriptCode] ASC);

