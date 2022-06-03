CREATE TABLE [dbo].[FormatInputDef] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [CharPos]        INT          NULL,
    [CharType]       VARCHAR (2)  NULL,
    [AllowedChars]   VARCHAR (50) NULL,
    [Case]           VARCHAR (1)  NULL
);


GO
CREATE NONCLUSTERED INDEX [I_FORMATINPUTDEF]
    ON [dbo].[FormatInputDef]([ScriptID] ASC, [SequenceNumber] ASC, [CharPos] ASC);

