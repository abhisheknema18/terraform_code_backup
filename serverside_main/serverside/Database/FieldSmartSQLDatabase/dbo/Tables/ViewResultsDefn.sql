CREATE TABLE [dbo].[ViewResultsDefn] (
    [ViewID]         INT          NOT NULL,
    [ScriptID]       INT          NOT NULL,
    [SequenceNumber] INT          NOT NULL,
    [SearchType]     VARCHAR (8)  NOT NULL,
    [Operator]       VARCHAR (8)  NULL,
    [FieldValue]     VARCHAR (40) NULL
);


GO
CREATE NONCLUSTERED INDEX [I_VIEWRESULTSDEFN_FILTER]
    ON [dbo].[ViewResultsDefn]([ViewID] ASC, [ScriptID] ASC, [SequenceNumber] ASC, [SearchType] ASC);

