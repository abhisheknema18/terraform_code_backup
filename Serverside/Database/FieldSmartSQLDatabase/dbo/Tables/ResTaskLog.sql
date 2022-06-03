CREATE TABLE [dbo].[ResTaskLog] (
    [ReturnID]         INT          NOT NULL,
    [EditRefNo]        INT          NOT NULL,
    [TaskOrderNo]      INT          NOT NULL,
    [TaskDesc]         VARCHAR (80) NULL,
    [TaskCompleteCode] VARCHAR (2)  NULL,
    CONSTRAINT [PK_RESTASKLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditRefNo] ASC, [TaskOrderNo] ASC)
);

