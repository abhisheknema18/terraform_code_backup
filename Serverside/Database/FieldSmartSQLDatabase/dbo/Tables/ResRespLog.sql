CREATE TABLE [dbo].[ResRespLog] (
    [ReturnID]  INT            NOT NULL,
    [EditRefNo] INT            NOT NULL,
    [Response]  VARCHAR (1250) NULL,
    [UOM]       VARCHAR (12)   NULL,
    CONSTRAINT [PK_RESRESPLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditRefNo] ASC)
);

