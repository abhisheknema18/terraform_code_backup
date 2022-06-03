CREATE TABLE [dbo].[WorkStatusHistory] (
    [WorkOrderNo]    VARCHAR (20)  NOT NULL,
    [DistrictCode]   VARCHAR (4)   NOT NULL,
    [WorkStatus]     VARCHAR (40)  NOT NULL,
    [WorkStatusDate] INT           NOT NULL,
    [WorkStatusTime] INT           NOT NULL,
    [UserCode]       VARCHAR (32)  NOT NULL,
    [AdditionalText] VARCHAR (120) NULL,
    [TransFileName]  VARCHAR (255) NULL,
    CONSTRAINT [PK_WORKSTATUSHISTORY] PRIMARY KEY CLUSTERED ([WorkOrderNo] ASC, [DistrictCode] ASC, [WorkStatus] ASC, [WorkStatusDate] ASC, [WorkStatusTime] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_WORKSTATUSHISTORY_SESSDIS]
    ON [dbo].[WorkStatusHistory]([WorkOrderNo] ASC, [DistrictCode] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKSTATUSHISTORY_TRANS]
    ON [dbo].[WorkStatusHistory]([TransFileName] ASC);

