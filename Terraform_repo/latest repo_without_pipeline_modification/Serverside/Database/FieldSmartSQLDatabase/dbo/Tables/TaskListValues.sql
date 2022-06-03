CREATE TABLE [dbo].[TaskListValues] (
    [TaskListId]       INT          NOT NULL,
    [TaskOrderNo]      INT          NOT NULL,
    [TaskDesc]         VARCHAR (80) NULL,
    [TaskCompleteCode] VARCHAR (2)  NULL,
    CONSTRAINT [PK_TASKLISTVALUES] PRIMARY KEY CLUSTERED ([TaskListId] ASC, [TaskOrderNo] ASC)
);

