CREATE TABLE [dbo].[InterfaceQueue] (
    [ReturnID]     INT          NOT NULL,
    [ScriptCodeID] INT          NOT NULL,
    [ResultStatus] VARCHAR (15) NOT NULL,
    CONSTRAINT [PK_INTERFACEQUEUE] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ScriptCodeID] ASC, [ResultStatus] ASC)
);

