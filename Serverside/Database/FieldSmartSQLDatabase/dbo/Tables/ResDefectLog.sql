CREATE TABLE [dbo].[ResDefectLog] (
    [ReturnID]   INT         NOT NULL,
    [EditRefNo]  INT         NOT NULL,
    [DefectCode] VARCHAR (4) NOT NULL,
    [Action]     VARCHAR (2) NULL,
    [Severity]   VARCHAR (2) NULL,
    CONSTRAINT [PK_RESDEFECTLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditRefNo] ASC, [DefectCode] ASC)
);

