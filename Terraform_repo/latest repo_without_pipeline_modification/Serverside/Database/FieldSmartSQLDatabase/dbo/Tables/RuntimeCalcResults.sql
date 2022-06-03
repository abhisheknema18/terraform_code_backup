CREATE TABLE [dbo].[RuntimeCalcResults] (
    [ReturnID]         INT             NOT NULL,
    [CalculationName]  VARCHAR (20)    NOT NULL,
    [CalculationValue] DECIMAL (14, 5) NOT NULL,
    CONSTRAINT [PK_RuntimeCalcResults] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [CalculationName] ASC)
);

