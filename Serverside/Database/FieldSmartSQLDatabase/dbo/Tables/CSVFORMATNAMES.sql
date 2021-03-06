CREATE TABLE [dbo].[CSVFORMATNAMES] (
    [FORMATID]     INT          NOT NULL,
    [FORMATNAME]   VARCHAR (30) NOT NULL,
    [FORMATDESC]   VARCHAR (80) NOT NULL,
    [CREATEUSER]   VARCHAR (10) NOT NULL,
    [CREATEDATE]   INT          NOT NULL,
    [AVAILFLAG]    VARCHAR (1)  NULL,
    [BASETYPE]     VARCHAR (15) NULL,
    [SCRIPTID]     INT          NULL,
    [SCRIPTCODEID] INT          NULL,
    CONSTRAINT [PK_CSVFORMATNAMES] PRIMARY KEY CLUSTERED ([FORMATID] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_CSVFORMATNAMES_SCRIPTID]
    ON [dbo].[CSVFORMATNAMES]([SCRIPTID] ASC);


GO
CREATE NONCLUSTERED INDEX [I_CSVFORMATNAMES_SCRIPTCODEID]
    ON [dbo].[CSVFORMATNAMES]([SCRIPTCODEID] ASC);

