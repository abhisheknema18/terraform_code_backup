CREATE TABLE [dbo].[RuntimeParameters] (
    [ScriptID]      INT           NOT NULL,
    [ParameterName] VARCHAR (20)  NOT NULL,
    [ParameterDef]  VARCHAR (200) NOT NULL,
    CONSTRAINT [PK_RuntimeParameters] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [ParameterName] ASC)
);

