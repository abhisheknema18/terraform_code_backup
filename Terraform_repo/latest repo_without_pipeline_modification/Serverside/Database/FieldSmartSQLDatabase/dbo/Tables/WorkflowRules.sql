CREATE TABLE [dbo].[WorkflowRules] (
    [RuleID]       INT          NOT NULL,
    [ScriptID]     INT          NOT NULL,
    [RuleName]     VARCHAR (50) NOT NULL,
    [RuleOrderNo]  INT          NOT NULL,
    [TargetStatus] VARCHAR (15) NOT NULL,
    [LogicOp]      VARCHAR (3)  NOT NULL,
    CONSTRAINT [PK_WorkflowRules] PRIMARY KEY CLUSTERED ([RuleID] ASC)
);

