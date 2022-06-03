CREATE TABLE [dbo].[CarryThrough] (
    [ScriptCodeID] INT          NOT NULL,
    [FieldName]    VARCHAR (40) NOT NULL,
    [FieldLabel]   VARCHAR (50) NULL,
    [InfoOnly]     INT          NULL,
    [FieldType]    VARCHAR (1)  NOT NULL,
    [FieldValue]   VARCHAR (80) NULL,
    CONSTRAINT [PK_CARRYTHROUGH] PRIMARY KEY CLUSTERED ([ScriptCodeID] ASC, [FieldName] ASC)
);

