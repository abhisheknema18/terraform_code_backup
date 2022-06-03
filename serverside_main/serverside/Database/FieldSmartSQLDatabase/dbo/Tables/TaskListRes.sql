CREATE TABLE [dbo].[TaskListRes] (
    [ReturnID]       INT NOT NULL,
    [SequenceNumber] INT NOT NULL,
    [ResorderNo]     INT NOT NULL,
    [TaskListResID]  INT NOT NULL,
    CONSTRAINT [PK_TASKLISTRES] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResorderNo] ASC, [SequenceNumber] ASC, [TaskListResID] ASC)
);


GO
CREATE UNIQUE NONCLUSTERED INDEX [UI_TASKLISTRESID]
    ON [dbo].[TaskListRes]([TaskListResID] ASC);

