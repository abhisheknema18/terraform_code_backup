CREATE TABLE [dbo].[CSVFieldDefn] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [FieldNo]        INT          NOT NULL,
    [FieldLabel]     VARCHAR (40) NULL,
    [Searchable]     VARCHAR (1)  NULL,
    CONSTRAINT [PK_CSVFIELDDEFN] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [FieldNo] ASC)
);

