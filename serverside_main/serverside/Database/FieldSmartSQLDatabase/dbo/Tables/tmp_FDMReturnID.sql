CREATE TABLE [dbo].[tmp_FDMReturnID] (
    [SessionID] VARCHAR (50) NOT NULL,
    [ReturnID]  INT          NOT NULL
);


GO
CREATE NONCLUSTERED INDEX [TMP_FDMRETURNID_SID]
    ON [dbo].[tmp_FDMReturnID]([SessionID] ASC);


GO
CREATE NONCLUSTERED INDEX [TMP_FDMRETURNID_RID]
    ON [dbo].[tmp_FDMReturnID]([ReturnID] ASC);

