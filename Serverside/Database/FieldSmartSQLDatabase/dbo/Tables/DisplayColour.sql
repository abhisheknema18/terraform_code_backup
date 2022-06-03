CREATE TABLE [dbo].[DisplayColour] (
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [Response]       VARCHAR (80) NOT NULL,
    [Colour]         VARCHAR (12) NULL,
    [ValidationType] VARCHAR (15) NOT NULL,
    CONSTRAINT [PK_DISPLAYCOLOUR] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [ValidationType] ASC, [Response] ASC)
);

