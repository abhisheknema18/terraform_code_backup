CREATE TABLE [dbo].[WorkIssued] (
    [WorkOrderNo]    VARCHAR (20)  NOT NULL,
    [WorkOrderDesc]  VARCHAR (80)  NULL,
    [IssuedTime]     INT           NULL,
    [EquipNo]        VARCHAR (30)  NULL,
    [EquipDesc]      VARCHAR (80)  NULL,
    [AltEquipRef]    VARCHAR (80)  NULL,
    [PlanStartDate]  INT           NULL,
    [PlanStartTime]  VARCHAR (6)   NULL,
    [ReqFinishDate]  INT           NULL,
    [ReqFinishTime]  VARCHAR (6)   NULL,
    [UserCode]       VARCHAR (32)  NULL,
    [WoType]         VARCHAR (18)  NULL,
    [SourceFileName] VARCHAR (255) NULL,
    [ReturnID]       INT           NULL,
    [DistrictCode]   VARCHAR (4)   NOT NULL,
    [WorkStatus]     VARCHAR (40)  NULL,
    [WorkStatusDate] INT           NULL,
    [WorkStatusTime] INT           NULL,
    [AdditionalText] VARCHAR (500) NULL,
    [IssuedDate]     INT           NOT NULL,
    [WorkGroupCode]  VARCHAR (10)  NOT NULL,
    [Latitude]       VARCHAR (255) NULL,
    [Longitude]      VARCHAR (255) NULL,
    CONSTRAINT [PK_WORKISSUED] PRIMARY KEY CLUSTERED ([WorkOrderNo] ASC, [DistrictCode] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_SOURCEFILENAME]
    ON [dbo].[WorkIssued]([SourceFileName] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_RID]
    ON [dbo].[WorkIssued]([ReturnID] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_STATUS]
    ON [dbo].[WorkIssued]([WorkStatus] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_WORKGROUPCODE]
    ON [dbo].[WorkIssued]([WorkGroupCode] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_USERCODE]
    ON [dbo].[WorkIssued]([UserCode] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_ISSUEDDATE]
    ON [dbo].[WorkIssued]([IssuedDate] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_EQUIPNO]
    ON [dbo].[WorkIssued]([EquipNo] ASC);


GO
CREATE NONCLUSTERED INDEX [I_WORKISSUED_IDX1]
    ON [dbo].[WorkIssued]([WorkGroupCode] ASC, [UserCode] ASC, [WorkStatus] ASC, [WoType] ASC, [IssuedDate] ASC, [IssuedTime] ASC, [ReqFinishDate] ASC);

