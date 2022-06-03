CREATE TABLE [dbo].[ReturnedScripts] (
    [ReturnID]         INT           NOT NULL,
    [ScriptID]         INT           NOT NULL,
    [ScriptCode]       VARCHAR (15)  NOT NULL,
    [ScriptStatus]     VARCHAR (5)   NOT NULL,
    [Project]          VARCHAR (30)  NULL,
    [DeviceID]         VARCHAR (255) NULL,
    [WorkOrderNo]      VARCHAR (20)  NULL,
    [WorkOrderDesc]    VARCHAR (80)  NULL,
    [EquipNo]          VARCHAR (30)  NULL,
    [EquipDesc]        VARCHAR (80)  NULL,
    [AltEquipRef]      VARCHAR (30)  NULL,
    [SummaryDesc]      VARCHAR (80)  NULL,
    [RunType]          VARCHAR (2)   NULL,
    [StartDate]        INT           NULL,
    [StartTime]        INT           NULL,
    [CompleteDate]     INT           NULL,
    [CompleteTime]     INT           NULL,
    [Duration]         INT           NULL,
    [CompleteUser]     VARCHAR (32)  NULL,
    [CompleteCode]     VARCHAR (5)   NULL,
    [WorkGroup]        VARCHAR (30)  NULL,
    [ReturnDate]       INT           NULL,
    [ResultStatus]     VARCHAR (15)  NULL,
    [ReturnFile]       VARCHAR (255) NULL,
    [UploadStatus]     VARCHAR (15)  NULL,
    [UploadDate]       INT           NULL,
    [ResAssocCode]     VARCHAR (70)  NULL,
    [TotalWeightScore] INT           NULL,
    CONSTRAINT [PK_RETURNEDSCRIPTS] PRIMARY KEY CLUSTERED ([ReturnID] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_COMPLETEDATE]
    ON [dbo].[ReturnedScripts]([CompleteDate] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_COMPLETEUSER]
    ON [dbo].[ReturnedScripts]([CompleteUser] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_EQUIPNO]
    ON [dbo].[ReturnedScripts]([EquipNo] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_RESASSOCCODE]
    ON [dbo].[ReturnedScripts]([ResAssocCode] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_RESULTSTATUS]
    ON [dbo].[ReturnedScripts]([ResultStatus] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_SCRIPTID]
    ON [dbo].[ReturnedScripts]([ScriptID] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_SCRIPTSTATUS]
    ON [dbo].[ReturnedScripts]([ScriptStatus] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_WORKGROUP]
    ON [dbo].[ReturnedScripts]([WorkGroup] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_WORKORDERNO]
    ON [dbo].[ReturnedScripts]([WorkOrderNo] ASC);


GO
CREATE UNIQUE NONCLUSTERED INDEX [UI_RETURNEDSCRIPTS_RETURNFILE]
    ON [dbo].[ReturnedScripts]([ReturnFile] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_ALTEQUIPREF]
    ON [dbo].[ReturnedScripts]([AltEquipRef] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RETURNEDSCRIPTS_IDX1]
    ON [dbo].[ReturnedScripts]([ScriptCode] ASC, [WorkGroup] ASC, [ResultStatus] ASC, [CompleteDate] ASC, [CompleteTime] ASC);

