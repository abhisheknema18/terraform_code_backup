CREATE TABLE [dbo].[ViewCTDefn] (
    [ViewId]       INT          NOT NULL,
    [ScriptCodeId] INT          NOT NULL,
    [FieldName]    VARCHAR (40) NOT NULL,
    [FieldValue]   VARCHAR (40) NOT NULL,
    [Operator]     VARCHAR (2)  NULL,
    CONSTRAINT [PK_VIEWCTDEFN] PRIMARY KEY CLUSTERED ([ViewId] ASC, [ScriptCodeId] ASC, [FieldName] ASC, [FieldValue] ASC)
);

