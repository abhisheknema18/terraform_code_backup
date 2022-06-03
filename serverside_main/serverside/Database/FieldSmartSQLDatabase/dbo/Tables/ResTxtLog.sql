CREATE TABLE [dbo].[ResTxtLog] (
    [ReturnID]  INT           NOT NULL,
    [EditrefNo] INT           NOT NULL,
    [FreeText]  VARCHAR (200) NULL,
    CONSTRAINT [PK_RESTXTLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditrefNo] ASC)
);

