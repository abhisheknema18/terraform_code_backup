CREATE TABLE [dbo].[WorkflowRulesDef] (
    [RuleDefID] INT          NOT NULL,
    [RuleID]    INT          NOT NULL,
    [RuleItem]  VARCHAR (20) NOT NULL,
    [Op]        VARCHAR (3)  NOT NULL,
    [RuleValue] VARCHAR (80) NOT NULL,
    CONSTRAINT [PK_WorkflowRulesDef] PRIMARY KEY CLUSTERED ([RuleDefID] ASC)
);

