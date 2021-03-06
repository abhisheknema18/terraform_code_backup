CREATE TABLE [dbo].[CSVFORMATDEFN] (
    [CSVFORMATID]     INT          NOT NULL,
    [CSVFIELDORDERNO] INT          NOT NULL,
    [CSVFIELDTYPE]    VARCHAR (2)  NOT NULL,
    [CSVFIELDVALUE]   VARCHAR (50) NULL,
    CONSTRAINT [PK_CSVFORMATDEFN] PRIMARY KEY CLUSTERED ([CSVFORMATID] ASC, [CSVFIELDORDERNO] ASC, [CSVFIELDTYPE] ASC)
);

