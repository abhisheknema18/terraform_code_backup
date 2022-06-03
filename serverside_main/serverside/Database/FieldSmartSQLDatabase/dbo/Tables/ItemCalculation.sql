CREATE TABLE [dbo].[ItemCalculation] (
    [ScriptID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [Formula]        VARCHAR (300) NULL,
    CONSTRAINT [PK_ITEMCALCULATION] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);

