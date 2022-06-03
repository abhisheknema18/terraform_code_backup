CREATE TABLE [dbo].[WorkIssuedFileRefs] (
    [WorkOrderNo]  VARCHAR (20)  NOT NULL,
    [DistrictCode] VARCHAR (4)   NOT NULL,
    [FileName]     VARCHAR (255) NOT NULL,
    [Type]         VARCHAR (4)   NOT NULL,
    [Description]  VARCHAR (255) NULL,
    CONSTRAINT [PK_WorkIssuedFileRefs] PRIMARY KEY CLUSTERED ([WorkOrderNo] ASC, [DistrictCode] ASC, [FileName] ASC)
);

