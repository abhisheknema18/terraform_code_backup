CREATE TABLE [dbo].[Retscript_Edit_Log] (
    [NewReturnID]  INT          NOT NULL,
    [PrevReturnID] INT          NOT NULL,
    [EditUser]     VARCHAR (10) NOT NULL,
    [EditDate]     INT          NOT NULL,
    [EditTime]     INT          NOT NULL,
    CONSTRAINT [PK_RETSCRIPT_EDIT_LOG] PRIMARY KEY CLUSTERED ([NewReturnID] ASC)
);


GO
CREATE UNIQUE NONCLUSTERED INDEX [UI_RETSCRIPT_EDIT_LOG_IDX1]
    ON [dbo].[Retscript_Edit_Log]([PrevReturnID] ASC);

