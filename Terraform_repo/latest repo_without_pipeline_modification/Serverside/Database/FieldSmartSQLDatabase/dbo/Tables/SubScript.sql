CREATE TABLE [dbo].[SubScript] (
    [ScriptID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ScriptCode]     VARCHAR (15)  NOT NULL,
    [MenuText]       VARCHAR (100) NOT NULL,
    CONSTRAINT [PK_SUBSCRIPT] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [ScriptCode] ASC)
);

