CREATE TABLE [dbo].[RuntimeCalculations] (
    [ScriptID]        INT           NOT NULL,
    [CalculationName] VARCHAR (20)  NOT NULL,
    [CalculationDesc] VARCHAR (100) NULL,
    [Formula]         VARCHAR (300) NOT NULL,
    [Precision]       INT           NOT NULL,
    [EvaluationType]  VARCHAR (3)   NOT NULL,
    CONSTRAINT [PK_RuntimeCalculations] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [CalculationName] ASC)
);

