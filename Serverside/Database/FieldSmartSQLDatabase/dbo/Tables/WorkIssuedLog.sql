CREATE TABLE [dbo].[WorkIssuedLog] (
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
    [DistrictCode]   VARCHAR (4)   NOT NULL,
    [AdditionalText] VARCHAR (500) NULL,
    [IssuedDate]     INT           NOT NULL,
    [WorkGroupCode]  VARCHAR (10)  NOT NULL,
    [Latitude]       VARCHAR (255) NULL,
    [Longitude]      VARCHAR (255) NULL,
    [LogUser]        VARCHAR (32)  NOT NULL,
    [LogDate]        INT           NOT NULL,
    [LogTime]        VARCHAR (6)   NOT NULL,
    CONSTRAINT [PK_WorkIssuedLog] PRIMARY KEY CLUSTERED ([WorkOrderNo] ASC, [DistrictCode] ASC, [LogUser] ASC, [LogDate] ASC, [LogTime] ASC)
);

