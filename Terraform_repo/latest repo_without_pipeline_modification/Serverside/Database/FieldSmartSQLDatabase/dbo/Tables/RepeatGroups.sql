CREATE TABLE [dbo].[RepeatGroups] (
    [ScriptID]        INT          NOT NULL,
    [FromSeqNo]       INT          NOT NULL,
    [ToSeqNo]         INT          NULL,
    [NoOfGroups]      VARCHAR (20) NULL,
    [RepeatType]      VARCHAR (2)  NULL,
    [TriggerResponse] VARCHAR (40) NULL,
    CONSTRAINT [PK_REPEATGROUPS] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [FromSeqNo] ASC)
);

