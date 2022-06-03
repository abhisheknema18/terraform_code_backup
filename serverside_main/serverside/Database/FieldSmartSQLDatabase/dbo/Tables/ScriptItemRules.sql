CREATE TABLE [dbo].[ScriptItemRules] (
    [ScriptID]   INT           NOT NULL,
    [FieldName]  VARCHAR (50)  NOT NULL,
    [Op]         VARCHAR (2)   NOT NULL,
    [FieldValue] VARCHAR (100) NOT NULL,
    [FromSeqNo]  INT           NOT NULL,
    [ToSeqNo]    INT           NULL,
    CONSTRAINT [PK_SCRIPTITEMRULES] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [FromSeqNo] ASC, [Op] ASC, [FieldName] ASC, [FieldValue] ASC)
);

