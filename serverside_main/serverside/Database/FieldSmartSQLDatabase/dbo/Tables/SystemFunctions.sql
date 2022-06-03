CREATE TABLE [dbo].[SystemFunctions] (
    [ProgCode]     VARCHAR (4)  NOT NULL,
    [FunctionCode] VARCHAR (6)  NOT NULL,
    [FunctionDesc] VARCHAR (30) NOT NULL,
    CONSTRAINT [PK_SYSTEMFUNCTIONS] PRIMARY KEY CLUSTERED ([ProgCode] ASC, [FunctionCode] ASC)
);

